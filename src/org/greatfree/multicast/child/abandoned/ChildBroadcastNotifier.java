package org.greatfree.multicast.child.abandoned;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.ChildBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;

/*
 * The multicastor is derived from ChildMulticastor to transfer the notification, to the children nodes. 11/27/2014, Bing Li
 */

// Created: 05/05/2017, Bing Li
class ChildBroadcastNotifier<Notification extends OldMulticastMessage, NotificationCreator extends ChildBroadcastNotificationCreatable<Notification>> extends SubBroadcastNotifier<Notification, NotificationCreator>
{

	public ChildBroadcastNotifier(String localIPKey, FreeClientPool clientPool, int treeBranchCount, NotificationCreator messageCreator)
	{
		super(localIPKey, clientPool, treeBranchCount, messageCreator);
	}
	/*
	public ChildBroadcastNotifier(FreeClientPool clientPool, int treeBranchCount, NotificationCreator messageCreator)
	{
		super(clientPool, treeBranchCount, messageCreator);
	}
	*/
}
