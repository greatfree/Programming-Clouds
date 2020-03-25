package org.greatfree.multicast.child.abandoned;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.ChildBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.reuse.ResourcePool;

/*
 * The class works as the child of the cluster to keep send the request to its sub children. 05/07/2017, Bing Li
 */

// Created: 05/07/2017, Bing Li
public class ClusterChildBroadcastReader<Request extends OldMulticastRequest, RequestCreator extends ChildBroadcastRequestCreatable<Request>>
{
	// The TCP client pool that sends requests. 05/04/2017, Bing Li
	private FreeClientPool clientPool;

	// The request creator. 05/04/2017, Bing Li
	private RequestCreator requestCreator;
	
	// The children TCP port. 05/05/2017, Bing Li
//	private final int port;

	// The pool for the multicastor which broadcasts the request. 05/04/2017, Bing Li
	private ResourcePool<ChildBroadcastReaderSource<Request, RequestCreator>, ChildBroadcastReader<Request, RequestCreator>, ChildBroadcastReaderCreator<Request, RequestCreator>, ChildBroadcastReaderDisposer<Request, RequestCreator>> requestBroadcastorPool;

	// The local IP key. The key is used to avoid the local node sends messages to itself. 05/19/2017, Bing Li
	private final String localIPKey;

	/*
	 * Dispose the sub broadcast reader. 05/07/2017, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		this.requestBroadcastorPool.shutdown();
	}

	/*
	 * Initialize the reader. 05/07/2017, Bing Li
	 */
//	public ClusterChildBroadcastReader(FreeClientPool clientPool, int port, RequestCreator requestCreator, int poolSize, long readerAssignWaitTime)
	public ClusterChildBroadcastReader(String localIPKey, FreeClientPool clientPool, int poolSize, long readerAssignWaitTime, RequestCreator requestCreator)
	{
		this.localIPKey = localIPKey;
		this.clientPool = clientPool;
//		this.port = port;
		this.requestCreator = requestCreator;
		
		this.requestBroadcastorPool = new ResourcePool<ChildBroadcastReaderSource<Request, RequestCreator>, ChildBroadcastReader<Request, RequestCreator>, ChildBroadcastReaderCreator<Request, RequestCreator>, ChildBroadcastReaderDisposer<Request, RequestCreator>>(poolSize, new ChildBroadcastReaderCreator<Request, RequestCreator>(), new ChildBroadcastReaderDisposer<Request, RequestCreator>(), readerAssignWaitTime);
	}
	
	/*
	 * Disseminate the notification of Request to the children node of the local one. 05/07/2017, Bing Li
	 */
//	public void read(Request request, int branchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	public void read(Request request, int branchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Get an instance of SubBroadcastReader from the pool. 05/07/2017, Bing Li
		ChildBroadcastReader<Request, RequestCreator> reader = this.requestBroadcastorPool.get(new ChildBroadcastReaderSource<Request, RequestCreator>(this.localIPKey, this.clientPool, branchCount, this.requestCreator));
		// Check whether the notifier is valid. 05/07/2017, Bing Li
		if (reader != null)
		{
			// Disseminate the request. 05/07/2017, Bing Li
			reader.disseminate(request);
			// Collect the notifier. 05/07/2017, Bing Li
			this.requestBroadcastorPool.collect(reader);
		}
	}
}
