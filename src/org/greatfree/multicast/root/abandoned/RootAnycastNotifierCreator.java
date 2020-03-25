package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.reuse.HashCreatable;

/*
 * The class intends to create the instance of RootAnycastNotifier. It is used by the resource pool to manage the anycastors efficiently. 05/04/2017, Bing Li
 */

// Created: 05/04/2017, Bing Li
class RootAnycastNotifierCreator<Data extends Serializable, Notification extends OldMulticastMessage, NotificationCreator extends RootBroadcastNotificationCreatable<Notification, Data>> implements HashCreatable<RootAnycastNotifierSource<Data, Notification, NotificationCreator>, RootAnycastNotifier<Data, Notification, NotificationCreator>>
{

	@Override
	public RootAnycastNotifier<Data, Notification, NotificationCreator> createResourceInstance(RootAnycastNotifierSource<Data, Notification, NotificationCreator> source)
	{
		return new RootAnycastNotifier<Data, Notification, NotificationCreator>(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator());
	}
}
