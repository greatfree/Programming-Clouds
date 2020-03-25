package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;

/*
 * The class is an eventer as well. But different from the classic eventer, the eventer handles the events happened between one and multiple nodes. 05/04/2017, Bing Li
 */

// Created: 05/04/2017, Bing Li
class RootBroadcastNotifier<Data extends Serializable, Notification extends OldMulticastMessage, NotificationCreator extends RootBroadcastNotificationCreatable<Notification, Data>> extends BaseBroadcastNotifier<Data, Notification, NotificationCreator>
{

	public RootBroadcastNotifier(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, NotificationCreator messageCreator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, messageCreator);
	}
}
