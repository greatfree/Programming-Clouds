package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;

/*
 * The class contains all of the information that is required to create an instance of RootMulticastor. It is needed in the relevant pool. 11/26/2014, Bing Li
 */

// Created: 05/12/2017, Bing Li
abstract class BaseBroadcastNotifierSource<Source extends Serializable, Notification extends OldMulticastMessage, NotificationCreator extends RootBroadcastNotificationCreatable<Notification, Source>>
{
	// The pool for the resources of FreeClient. 11/26/2014, Bing Li
	private FreeClientPool clientPool;
	// The size of the root branch. 11/26/2014, Bing Li
	private int rootBranchCount;
	// The size of the tree branch. 11/26/2014, Bing Li
	private int treeBranchCount;
	// The message creator that generates the multicast messages. 11/26/2014, Bing Li
	private NotificationCreator creator;
	
	/*
	 * Initialize the source to provide arguments to create multicastors. 11/26/2014, Bing Li
	 */
	public BaseBroadcastNotifierSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, NotificationCreator creator)
	{
		this.clientPool = clientPool;
		this.rootBranchCount = rootBranchCount;
		this.treeBranchCount = treeBranchCount;
		this.creator = creator;
	}

	/*
	 * Expose the client pool. 11/26/2014, Bing Li
	 */
	public FreeClientPool getClientPool()
	{
		return this.clientPool;
	}

	/*
	 * Expose the root branch count. 11/26/2014, Bing Li
	 */
	public int getRootBranchCount()
	{
		return this.rootBranchCount;
	}

	/*
	 * Expose the tree branch count. 11/26/2014, Bing Li
	 */
	public int getTreeBranchCount()
	{
		return this.treeBranchCount;
	}

	/*
	 * Expose the message creator. 11/26/2014, Bing Li
	 */
	public NotificationCreator getCreator()
	{
		return this.creator;
	}
}
