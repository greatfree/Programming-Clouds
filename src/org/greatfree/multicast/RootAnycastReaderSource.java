package org.greatfree.multicast;

import java.io.Serializable;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.abandoned.AnycastRequest;

/*
 * This class assists the resource pool to create instances of anycast readers. Thus, it contains all of required arguments to do that. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public abstract class RootAnycastReaderSource<Source extends Serializable, AnycastRequestMessage extends AnycastRequest, RequestCreator extends RootAnycastRequestCreatable<AnycastRequestMessage, Source>>
{
	// A TCP client pool to interact with remote nodes. 11/29/2014, Bing Li
	private FreeClientPool clientPool;
	// The root node's branch count, which can be understood as the capacity of the root node to send messages concurrently. The root is the broadcast request initial sender. 11/29/2014, Bing Li
	private int rootBranchCount;
	// The other node's branch count, which can be understood as the capacity of the nodes to send messages concurrently. In this case,  11/29/2014, Bing Li
	private int treeBranchCount;
	// The creator to generate anycast requests. 11/29/2014, Bing Li
	private RequestCreator creator;

	/*
	 * Initialize. 11/29/2014, Bing Li
	 */
	public RootAnycastReaderSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, RequestCreator creator)
	{
		this.clientPool = clientPool;
		this.rootBranchCount = rootBranchCount;
		this.treeBranchCount = treeBranchCount;
		this.creator = creator;
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
	public RequestCreator getCreator()
	{
		return this.creator;
	}
}
