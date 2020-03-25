package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;

/*
 * This class assists the resource pool to create instances of broadcast readers. Thus, it contains all of required arguments to do that. 11/29/2014, Bing Li
 */

// Created: 05/13/2017, Bing Li
abstract class BaseBroadcastReaderSource<Source extends Serializable, Request extends OldMulticastRequest, MessageCreator extends RootBroadcastRequestCreatable<Request, Source>>
{
	// A TCP client pool to interact with remote nodes. 11/29/2014, Bing Li
	private FreeClientPool clientPool;
	// The root node's branch count, which can be understood as the capacity of the root node to send messages concurrently. The root is the broadcast request initial sender. 11/29/2014, Bing Li
	private int rootBranchCount;
	// The other node's branch count, which can be understood as the capacity of the nodes to send messages concurrently. In this case,  11/29/2014, Bing Li
	private int treeBranchCount;
	// The creator to generate anycast requests. 11/29/2014, Bing Li
	private MessageCreator creator;
	// Since reading is a blocking method and performed with multiple nodes, it is necessary to set up the waiting time. If the waiting time is passed, the reading procedure should be terminated. It is not necessary to wait any longer. 05/05/2017, Bing Li
	private long waitTime;

	/*
	 * Initialize. 11/29/2014, Bing Li
	 */
	public BaseBroadcastReaderSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, MessageCreator creator, long waitTime)
	{
		this.clientPool = clientPool;
		this.rootBranchCount = rootBranchCount;
		this.treeBranchCount = treeBranchCount;
		this.creator = creator;
		this.waitTime = waitTime;
	}
	
	/*
	 * Expose the client pool. 11/29/2014, Bing Li
	 */
	public FreeClientPool getClientPool()
	{
		return this.clientPool;
	}
	
	/*
	 * Expose the root branch count. 11/29/2014, Bing Li
	 */
	public int getRootBranchCount()
	{
		return this.rootBranchCount;
	}
	
	/*
	 * Expose the tree branch count. 11/29/2014, Bing Li
	 */
	public int getTreeBranchCount()
	{
		return this.treeBranchCount;
	}
	
	/*
	 * Expose the request creator. 11/29/2014, Bing Li
	 */
	public MessageCreator getCreator()
	{
		return this.creator;
	}

	/*
	 * Expose the wait time. 05/05/2017, Bing Li
	 */
	public long getWaitTime()
	{
		return this.waitTime;
	}
}
