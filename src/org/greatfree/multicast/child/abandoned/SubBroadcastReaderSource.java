package org.greatfree.multicast.child.abandoned;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.ChildBroadcastRequestCreatable;
import org.greatfree.message.abandoned.OldMulticastRequest;

/*
 * The class contains all of the data to create an instance of ChildBroadcastReader. That is why it is named ChildBroadcastReaderSource. It is used by the resource pool to manage resources efficiently. 11/27/2014, Bing Li
 */

// Created: 05/13/2017, Bing Li
abstract class SubBroadcastReaderSource<Request extends OldMulticastRequest, MessageCreator extends ChildBroadcastRequestCreatable<Request>>
{
	// The local IP key. The key is used to avoid the local node sends messages to itself. 05/19/2017, Bing Li
	private final String localIPKey;
	// The pool for the resources of FreeClient. 11/27/2014, Bing Li
	private FreeClientPool clientPool;
	// The size of the tree branch. 11/27/2014, Bing Li
	private int treeBranchCount;
	// The message creator that generates the multicast messages. 11/27/2014, Bing Li
	private MessageCreator creator;
	
	/*
	 * Initialize the source to provide arguments to create multicastors. 11/27/2014, Bing Li
	 */
	public SubBroadcastReaderSource(String localIPKey, FreeClientPool clientPool, int treeBranchCount, MessageCreator creator)
	{
		this.localIPKey = localIPKey;
		this.clientPool = clientPool;
		this.treeBranchCount = treeBranchCount;
		this.creator = creator;
	}

	/*
	 * The IP key of the local node. 05/19/2017, Bing Li
	 */
	public String getLocalIPKey()
	{
		return this.localIPKey;
	}

	/*
	 * Expose the client pool. 11/27/2014, Bing Li
	 */
	public FreeClientPool getClientPool()
	{
		return this.clientPool;
	}
	
	/*
	 * Expose the tree branch count. 11/27/2014, Bing Li
	 */
	public int getTreeBranchCount()
	{
		return this.treeBranchCount;
	}
	
	/*
	 * Expose the message creator. 11/27/2014, Bing Li
	 */
	public MessageCreator getMessageCreator()
	{
		return this.creator;
	}
}
