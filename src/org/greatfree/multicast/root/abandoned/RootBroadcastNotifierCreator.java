package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.reuse.HashCreatable;

/*
 * The class intends to create the instance of RootBroadcastNotifier. It is used by the resource pool to manage the multicastors efficiently. 05/04/2017, Bing Li
 */

// Created: 05/04/2017, Bing Li
class RootBroadcastNotifierCreator<Data extends Serializable, Notification extends OldMulticastMessage, NotificationCreator extends RootBroadcastNotificationCreatable<Notification, Data>> implements HashCreatable<RootBroadcastNotifierSource<Data, Notification, NotificationCreator>, RootBroadcastNotifier<Data, Notification, NotificationCreator>>
{

	@Override
	public RootBroadcastNotifier<Data, Notification, NotificationCreator> createResourceInstance(RootBroadcastNotifierSource<Data, Notification, NotificationCreator> source)
	{
		return new RootBroadcastNotifier<Data, Notification, NotificationCreator>(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator());
	}

}
