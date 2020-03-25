package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.client.FreeClientPool;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;

/*
 * This is an extending of RootObjectMulticastor to transfer the notification of UnicastNotification to all of the DNs. 05/04/2017, Bing Li
 */

// Created: 05/04/2017, Bing Li
class RootUnicastNotifier<Data extends Serializable, Notification extends OldMulticastMessage, NotificationCreator extends RootBroadcastNotificationCreatable<Notification, Data>> extends BaseBroadcastNotifier<Data, Notification, NotificationCreator>
{
	/*
	 * Initialize the unicastor. 05/04/2017, Bing Li
	 */
	public RootUnicastNotifier(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, NotificationCreator messageCreator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, messageCreator);
	}

}
