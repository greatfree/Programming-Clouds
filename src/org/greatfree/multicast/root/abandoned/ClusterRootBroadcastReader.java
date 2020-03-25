package org.greatfree.multicast.root.abandoned;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.reuse.ResourcePool;

/*
 * This is a cluster reader located at the root of the cluster to perform the broadcast requests and obtain the responses. 05/05/2017, Bing Li
 */

// Created: 05/05/2017, Bing Li
public class ClusterRootBroadcastReader<Data extends Serializable, Request extends OldMulticastRequest, Response extends MulticastResponse, RequestCreator extends RootBroadcastRequestCreatable<Request, Data>>
{
	// The TCP client pool that sends requests. 05/04/2017, Bing Li
	private FreeClientPool clientPool;

	// The request creator. 05/04/2017, Bing Li
	private RequestCreator requestCreator;
	
	// The broadcast request waiting time. 05/07/2017, Bing Li
	private final long broadcastRequestWaitTime;

	// The collection to save the current working broadcast readers. 05/05/2017, Bing Li
	private Map<String, RootBroadcastReader<Data, Request, Response, RequestCreator>> broadcastReaders;
	// The resource pool to manage the instances of broadcast readers. 05/05/2017, Bing Li
	private ResourcePool<RootBroadcastReaderSource<Data, Request, RequestCreator>, RootBroadcastReader<Data, Request, Response, RequestCreator>, RootBroadcastReaderCreator<Data, Request, Response, RequestCreator>, RootBroadcastReaderDisposer<Data, Request, Response, RequestCreator>> broadcastReaderPool;

	/*
	 * The constructor. 05/05/2017, Bing Li
	 */
	public ClusterRootBroadcastReader(FreeClientPool clientPool, int poolSize, long readerAssignWaitTime, long broadcastRequestWaitTime, RequestCreator requestCreator)
	{
		// Initialize the client pool. 05/07/2017, Bing Li
		this.clientPool = clientPool;
		// Initialize the broadcast reader collection. 05/05/2017, Bing Li
		this.broadcastReaders = new ConcurrentHashMap<String, RootBroadcastReader<Data, Request, Response, RequestCreator>>();
		// Initialize the broadcastor pool. 05/05/2017, Bing Li
		this.broadcastReaderPool = new ResourcePool<RootBroadcastReaderSource<Data, Request, RequestCreator>, RootBroadcastReader<Data, Request, Response, RequestCreator>, RootBroadcastReaderCreator<Data, Request, Response, RequestCreator>, RootBroadcastReaderDisposer<Data, Request, Response, RequestCreator>>(poolSize, new RootBroadcastReaderCreator<Data, Request, Response, RequestCreator>(), new RootBroadcastReaderDisposer<Data, Request, Response, RequestCreator>(), readerAssignWaitTime);
		// Initialize the broadcast request waiting time. If the time period passes, the requesting can be terminated. 05/06/2017, Bing Li
		this.broadcastRequestWaitTime = broadcastRequestWaitTime;
		// Initialize the request creator. 05/07/2017, Bing Li
		this.requestCreator = requestCreator;
	}
	
	/*
	 * Dispose the resource pool for the reader. 05/04/2017, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		// Dispose each broadcast reader. 05/07/2017, Bing Li
		for (RootBroadcastReader<Data, Request, Response, RequestCreator> reader : this.broadcastReaders.values())
		{
			reader.dispose();
		}
		this.broadcastReaders.clear();
		// Shutdown the broadcast reader pool. 05/07/2017, Bing Li
		this.broadcastReaderPool.shutdown();
	}
	
	/*
	 * Requesting by broadcast. 05/05/2017, Bing Li
	 */
//	public Map<String, Response> read(IPAddress rootAddress, Data request, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	public Map<String, Response> read(Data request, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// Get one instance of the broadcast reader. 05/07/2017, Bing Li
		RootBroadcastReader<Data, Request, Response, RequestCreator> reader = this.broadcastReaderPool.get(new RootBroadcastReaderSource<Data, Request, RequestCreator>(this.clientPool, rootBranchCount, subBranchCount, this.requestCreator, this.broadcastRequestWaitTime));
		// Get the collaborator key of the reader. The key is unique to the reader. 05/07/2017, Bing Li
		String collaboratorKey = reader.resetBroadcast();
		// Save the instance of the broadcast reader. 05/07/2017, Bing Li
		this.broadcastReaders.put(collaboratorKey, reader);
		// Broadcast the request and wait for the responses. 05/07/2017, Bing Li
		Map<String, Response> responses = reader.broadcast(request);
		// After the responses are received, the reader must be collected. 05/07/2017, Bing Li
		this.broadcastReaderPool.collect(reader);
		// Remove the reader from the collection. 05/07/2017, Bing Li
		this.broadcastReaders.remove(collaboratorKey);
		// Return the responses. 05/07/2017, Bing Li
		return responses;
	}
	
	/*
	 * Requesting by broadcast for specified data keys. 05/05/2017, Bing Li
	 */
	public Map<String, Response> readNearestly(Set<String> dataKeys, Data request, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// Get one instance of the broadcast reader. 05/07/2017, Bing Li
		RootBroadcastReader<Data, Request, Response, RequestCreator> reader = this.broadcastReaderPool.get(new RootBroadcastReaderSource<Data, Request, RequestCreator>(this.clientPool, rootBranchCount, subBranchCount, this.requestCreator, this.broadcastRequestWaitTime));
		// Get the collaborator key of the reader. The key is unique to the reader. 05/07/2017, Bing Li
		String collaboratorKey = reader.resetBroadcast();
		// Save the instance of the broadcast reader. 05/07/2017, Bing Li
		this.broadcastReaders.put(collaboratorKey, reader);
		// Broadcast the request and wait for the responses. 05/07/2017, Bing Li
		Map<String, Response> responses = reader.broadcastNearestly(dataKeys, request);
		// After the responses are received, the reader must be collected. 05/07/2017, Bing Li
		this.broadcastReaderPool.collect(reader);
		// Remove the reader from the collection. 05/07/2017, Bing Li
		this.broadcastReaders.remove(collaboratorKey);
		// Return the responses. 05/07/2017, Bing Li
		return responses;
	}
	
	/*
	 * Once if a response to a broadcast request is received, it is required to save it in the reader until all of the node respond or the waiting time is over. 05/07/2017, Bing Li
	 */
	public void notifyResponseReceived(Response response)
	{
		// Check whether the response is the result to one of the reader by comparing the collaborator keys. 05/07/2017, Bing Li
		if (this.broadcastReaders.containsKey(response.getCollaboratorKey()))
		{
			// If the reader is waiting, save the response. The requesting process is waiting until all of the node respond or the waiting time is over. 05/07/2017, Bing Li
			this.broadcastReaders.get(response.getCollaboratorKey()).saveResponse(response);
		}
	}
}
