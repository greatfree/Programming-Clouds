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
 * This is the unicast reader that sends one request to a particular distributed node in a cluster and wait for its response. 05/07/2017, Bing Li
 */

// Created: 05/07/2017, Bing Li
public class ClusterRootUnicastReader<Data extends Serializable, Request extends OldMulticastRequest, Response extends MulticastResponse, RequestCreator extends RootBroadcastRequestCreatable<Request, Data>>
{
	// The TCP client pool that sends requests. 05/04/2017, Bing Li
	private FreeClientPool clientPool;

	// The request creator. 05/04/2017, Bing Li
	private RequestCreator requestCreator;
	
	// The broadcast request waiting time. 05/07/2017, Bing Li
	private final long unicastRequestWaitTime;

	// The collection to save the current working unicast readers. 05/05/2017, Bing Li
	private Map<String, RootUnicastReader<Data, Request, Response, RequestCreator>> unicastReaders;

	// The resource pool to manage the instances of broadcast readers. 05/05/2017, Bing Li
	private ResourcePool<RootUnicastReaderSource<Data, Request, RequestCreator>, RootUnicastReader<Data, Request, Response, RequestCreator>, RootUnicastReaderCreator<Data, Request, Response, RequestCreator>, RootUnicastReaderDisposer<Data, Request, Response, RequestCreator>> unicastReaderPool;

	/*
	 * The constructor. 05/05/2017, Bing Li
	 */
	public ClusterRootUnicastReader(FreeClientPool clientPool, int poolSize, long readerAssignWaitTime, long unicastRequestWaitTime, RequestCreator requestCreator)
	{
		// Initialize the client pool. 05/07/2017, Bing Li
		this.clientPool = clientPool;
		// Initialize the request creator. 05/07/2017, Bing Li
		this.requestCreator = requestCreator;
		// Initialize the broadcast reader collection. 05/05/2017, Bing Li
		this.unicastReaders = new ConcurrentHashMap<String, RootUnicastReader<Data, Request, Response, RequestCreator>>();
		// Initialize the unicastor pool. 05/05/2017, Bing Li
		this.unicastReaderPool = new ResourcePool<RootUnicastReaderSource<Data, Request, RequestCreator>, RootUnicastReader<Data, Request, Response, RequestCreator>, RootUnicastReaderCreator<Data, Request, Response, RequestCreator>, RootUnicastReaderDisposer<Data, Request, Response, RequestCreator>>(poolSize, new RootUnicastReaderCreator<Data, Request, Response, RequestCreator>(), new RootUnicastReaderDisposer<Data, Request, Response, RequestCreator>(), readerAssignWaitTime);
		// Initialize the unicast request waiting time. If the time period passes, the requesting can be terminated. 05/06/2017, Bing Li
		this.unicastRequestWaitTime = unicastRequestWaitTime;
	}

	/*
	 * Dispose the resource pool for the reader. 05/04/2017, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		// Dispose each broadcast reader. 05/07/2017, Bing Li
		for (RootUnicastReader<Data, Request, Response, RequestCreator> reader : this.unicastReaders.values())
		{
			reader.dispose();
		}
		this.unicastReaders.clear();
		// Shutdown the unicast reader pool. 05/07/2017, Bing Li
		this.unicastReaderPool.shutdown();
	}

	/*
	 * Requesting by unicast. 05/05/2017, Bing Li
	 */
//	public Map<String, Response> read(IPAddress rootAddress, Data request, String dnKey, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	public Map<String, Response> read(Data request, String dnKey, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// Get one instance of the unicast reader. 05/07/2017, Bing Li
		RootUnicastReader<Data, Request, Response, RequestCreator> reader = this.unicastReaderPool.get(new RootUnicastReaderSource<Data, Request, RequestCreator>(this.clientPool, rootBranchCount, subBranchCount, this.requestCreator, this.unicastRequestWaitTime));
		// Get the collaborator key of the reader. The key is unique to the reader. 05/07/2017, Bing Li
		String collaboratorKey = reader.resetBroadcast();
		// Save the instance of the unicast reader. 05/07/2017, Bing Li
		this.unicastReaders.put(collaboratorKey, reader);
		// Unicast the request and wait for the responses. 05/07/2017, Bing Li
		Map<String, Response> responses = reader.unicast(dnKey, request);
		// After the responses are received, the reader must be collected. 05/07/2017, Bing Li
		this.unicastReaderPool.collect(reader);
		// Remove the reader from the collection. 05/07/2017, Bing Li
		this.unicastReaders.remove(collaboratorKey);
		// Return the responses. 05/07/2017, Bing Li
		return responses;
	}

	/*
	 * Requesting by unicast. 05/05/2017, Bing Li
	 */
//	public Map<String, Response> read(IPAddress rootAddress, Data request, String dnKey, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	public Map<String, Response> read(Data request, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// Get one instance of the unicast reader. 05/07/2017, Bing Li
		RootUnicastReader<Data, Request, Response, RequestCreator> reader = this.unicastReaderPool.get(new RootUnicastReaderSource<Data, Request, RequestCreator>(this.clientPool, rootBranchCount, subBranchCount, this.requestCreator, this.unicastRequestWaitTime));
		// Get the collaborator key of the reader. The key is unique to the reader. 05/07/2017, Bing Li
		String collaboratorKey = reader.resetBroadcast();
		// Save the instance of the unicast reader. 05/07/2017, Bing Li
		this.unicastReaders.put(collaboratorKey, reader);
		// Unicast the request and wait for the responses. 05/07/2017, Bing Li
		Map<String, Response> responses = reader.unicast(request);
		// After the responses are received, the reader must be collected. 05/07/2017, Bing Li
		this.unicastReaderPool.collect(reader);
		// Remove the reader from the collection. 05/07/2017, Bing Li
		this.unicastReaders.remove(collaboratorKey);
		// Return the responses. 05/07/2017, Bing Li
		return responses;
	}

	/*
	 * Requesting by unicast. 05/05/2017, Bing Li
	 */
//	public Map<String, Response> read(IPAddress rootAddress, Data request, String dnKey, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	public Map<String, Response> readNearestly(String dataKey, Data request, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// Get one instance of the unicast reader. 05/07/2017, Bing Li
		RootUnicastReader<Data, Request, Response, RequestCreator> reader = this.unicastReaderPool.get(new RootUnicastReaderSource<Data, Request, RequestCreator>(this.clientPool, rootBranchCount, subBranchCount, this.requestCreator, this.unicastRequestWaitTime));
		// Get the collaborator key of the reader. The key is unique to the reader. 05/07/2017, Bing Li
		String collaboratorKey = reader.resetBroadcast();
		// Save the instance of the unicast reader. 05/07/2017, Bing Li
		this.unicastReaders.put(collaboratorKey, reader);
		// Unicast the request and wait for the responses. 05/07/2017, Bing Li
		Map<String, Response> responses = reader.unicastNearestly(dataKey, request);
		// After the responses are received, the reader must be collected. 05/07/2017, Bing Li
		this.unicastReaderPool.collect(reader);
		// Remove the reader from the collection. 05/07/2017, Bing Li
		this.unicastReaders.remove(collaboratorKey);
		// Return the responses. 05/07/2017, Bing Li
		return responses;
	}

	/*
	 * Once if a response to a unicast request is received, it is required to save it in the reader until all of the node respond or the waiting time is over. 05/07/2017, Bing Li
	 */
	public void notifyResponseReceived(Response response)
	{
		// Check whether the response is the result to one of the reader by comparing the collaborator keys. 05/07/2017, Bing Li
		if (this.unicastReaders.containsKey(response.getCollaboratorKey()))
		{
			// If the reader is waiting, save the response. The requesting process is waiting until all of the node respond or the waiting time is over. 05/07/2017, Bing Li
			this.unicastReaders.get(response.getCollaboratorKey()).saveResponse(response);
		}
	}
}
