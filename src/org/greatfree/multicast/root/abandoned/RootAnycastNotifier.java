package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;

/*
 * This is an extending of RootObjectMulticastor to transfer the notification of AnycastNotification to any of the DNs. 05/04/2017, Bing Li
 */

// Created: 05/04/2017, Bing Li
class RootAnycastNotifier<Data extends Serializable, Notification extends OldMulticastMessage, NotificationCreator extends RootBroadcastNotificationCreatable<Notification, Data>> extends BaseBroadcastNotifier<Data, Notification, NotificationCreator>
{

	public RootAnycastNotifier(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, NotificationCreator messageCreator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, messageCreator);
	}

}
