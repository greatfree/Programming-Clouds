package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.reuse.HashCreatable;

/*
 * The class intends to create the instance of RootUnicastNotifier. It is used by the resource pool to manage the unicastors efficiently. 05/04/2017, Bing Li
 */

// Created: 05/04/2017, Bing Li
class RootUnicastNotifierCreator<Data extends Serializable, Notification extends OldMulticastMessage, NotificationCreator extends RootBroadcastNotificationCreatable<Notification, Data>> implements HashCreatable<RootUnicastNotifierSource<Data, Notification, NotificationCreator>, RootUnicastNotifier<Data, Notification, NotificationCreator>>
{

	@Override
	public RootUnicastNotifier<Data, Notification, NotificationCreator> createResourceInstance(RootUnicastNotifierSource<Data, Notification, NotificationCreator> source)
	{
		return new RootUnicastNotifier<Data, Notification, NotificationCreator>(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator());
	}

}
