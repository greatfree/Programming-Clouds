package org.greatfree.multicast.root.abandoned;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.reuse.ResourcePool;

/*
 * This is a cluster reader located at the root of the cluster to perform the anycast requests and obtain the responses. 05/05/2017, Bing Li
 */

// Created: 05/07/2017, Bing Li
public class ClusterRootAnycastReader<Data extends Serializable, Request extends OldMulticastRequest, Response extends MulticastResponse, RequestCreator extends RootBroadcastRequestCreatable<Request, Data>>
{
	// The TCP client pool that sends requests. 05/04/2017, Bing Li
	private FreeClientPool clientPool;

	// The request creator. 05/04/2017, Bing Li
	private RequestCreator requestCreator;
	
	// The broadcast request waiting time. 05/07/2017, Bing Li
	private final long anycastRequestWaitTime;

	// The collection to save the current working anycast readers. 11/29/2014, Bing Li
	private Map<String, RootAnycastReader<Data, Request, Response, RequestCreator>> anycastReaders;

	// The resource pool to manage the instances of anycast readers. 11/29/2014, Bing Li
	private ResourcePool<RootAnycastReaderSource<Data, Request, RequestCreator>, RootAnycastReader<Data, Request, Response, RequestCreator>, RootAnycastReaderCreator<Data, Request, Response, RequestCreator>, RootAnycastReaderDisposer<Data, Request, Response, RequestCreator>> anycastReaderPool;

	/*
	 * The constructor. 05/05/2017, Bing Li
	 */
	public ClusterRootAnycastReader(FreeClientPool clientPool, int poolSize, long readerAssignWaitTime, long anycastRequestWaitTime, RequestCreator requestCreator)
	{
		// Initialize the client pool. 05/07/2017, Bing Li
		this.clientPool = clientPool;
		// Initialize the request creator. 05/07/2017, Bing Li
		this.requestCreator = requestCreator;
		// Initialize the anycast reader collection. 05/05/2017, Bing Li
		this.anycastReaders = new ConcurrentHashMap<String, RootAnycastReader<Data, Request, Response, RequestCreator>>();
		// Initialize the anycastor pool. 05/05/2017, Bing Li
		this.anycastReaderPool = new ResourcePool<RootAnycastReaderSource<Data, Request, RequestCreator>, RootAnycastReader<Data, Request, Response, RequestCreator>, RootAnycastReaderCreator<Data, Request, Response, RequestCreator>, RootAnycastReaderDisposer<Data, Request, Response, RequestCreator>>(poolSize, new RootAnycastReaderCreator<Data, Request, Response, RequestCreator>(), new RootAnycastReaderDisposer<Data, Request, Response, RequestCreator>(), readerAssignWaitTime);
		// Initialize the anycast request waiting time. If the time period passes, the requesting can be terminated. 05/06/2017, Bing Li
		this.anycastRequestWaitTime = anycastRequestWaitTime;
	}

	/*
	 * Dispose the resource pool for the reader. 05/04/2017, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		// Dispose each anycast reader. 05/07/2017, Bing Li
		for (RootAnycastReader<Data, Request, Response, RequestCreator> reader : this.anycastReaders.values())
		{
			reader.dispose();
		}
		this.anycastReaders.clear();
		// Shutdown the anycast reader pool. 05/07/2017, Bing Li
		this.anycastReaderPool.shutdown();
	}
	
	/*
	 * Requesting by anycast. 05/05/2017, Bing Li
	 */
//	public Map<String, Response> read(IPAddress rootAddress, Data request, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	public Map<String, Response> read(Data request, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// Get one instance of the anycast reader. 05/07/2017, Bing Li
		RootAnycastReader<Data, Request, Response, RequestCreator> reader = this.anycastReaderPool.get(new RootAnycastReaderSource<Data, Request, RequestCreator>(this.clientPool, rootBranchCount, subBranchCount, this.requestCreator, this.anycastRequestWaitTime));
		// Get the collaborator key of the reader. The key is unique to the reader. 05/07/2017, Bing Li
		String collaboratorKey = reader.resetBroadcast();
		// Save the instance of the anycast reader. 05/07/2017, Bing Li
		this.anycastReaders.put(collaboratorKey, reader);
		// Broadcast the request and wait for the responses. 05/07/2017, Bing Li
		Map<String, Response> responses = reader.anycast(request);
		// After the responses are received, the reader must be collected. 05/07/2017, Bing Li
		this.anycastReaderPool.collect(reader);
		// Remove the reader from the collection. 05/07/2017, Bing Li
		this.anycastReaders.remove(collaboratorKey);
		// Return the responses. 05/07/2017, Bing Li
		return responses;
	}

	/*
	 * Once if a response to an anycast request is received, it is required to save it in the reader until all of the node respond or the waiting time is over. 05/07/2017, Bing Li
	 */
	public void notifyResponseReceived(Response response)
	{
		// Check whether the response is the result to one of the reader by comparing the collaborator keys. 05/07/2017, Bing Li
		if (this.anycastReaders.containsKey(response.getCollaboratorKey()))
		{
			// If the reader is waiting, save the response. The requesting process is waiting until all of the node respond or the waiting time is over. 05/07/2017, Bing Li
			this.anycastReaders.get(response.getCollaboratorKey()).saveResponse(response);
		}
	}

}
