package org.greatfree.multicast.child.abandoned;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.ChildBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;

/*
 * The sources that are needed to create an instance of ChildBroadcastNotifier are enclosed in the class. That is required by the pool to create children broadcastor. 05/05/2017, Bing Li
  */

// Created: 05/05/2017, Bing Li
class ChildBroadcastNotifierSource<Notification extends OldMulticastMessage, NotificationCreator extends ChildBroadcastNotificationCreatable<Notification>> extends SubBroadcastNotifierSource<Notification, NotificationCreator>
{

	public ChildBroadcastNotifierSource(String localIPKey, FreeClientPool clientPool, int treeBranchCount, NotificationCreator creator)
	{
		super(localIPKey, clientPool, treeBranchCount, creator);
	}

	/*
	public ChildBroadcastNotifierSource(FreeClientPool clientPool, int treeBranchCount, NotificationCreator creator)
	{
		super(clientPool, treeBranchCount, creator);
	}
	*/
}
