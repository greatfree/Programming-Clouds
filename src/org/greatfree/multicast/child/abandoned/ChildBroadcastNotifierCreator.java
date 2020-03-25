package org.greatfree.multicast.child.abandoned;

import org.greatfree.message.ChildBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.reuse.HashCreatable;

/*
 * The class intends to create the interface of BroadcastNotifier. It is used by the resource pool to manage the children multicastors efficiently. 11/27/2014, Bing Li
 */


// Created: 05/05/2017, Bing Li
class ChildBroadcastNotifierCreator<Notification extends OldMulticastMessage, NotificationCreator extends ChildBroadcastNotificationCreatable<Notification>> implements HashCreatable<ChildBroadcastNotifierSource<Notification, NotificationCreator>, ChildBroadcastNotifier<Notification, NotificationCreator>>
{

	@Override
	public ChildBroadcastNotifier<Notification, NotificationCreator> createResourceInstance(ChildBroadcastNotifierSource<Notification, NotificationCreator> source)
	{
		return new ChildBroadcastNotifier<Notification, NotificationCreator>(source.getLocalIPKey(), source.getClientPool(), source.getTreeBranchCount(), source.getMessageCreator());
	}

	/*
	@Override
	public ChildBroadcastNotifier<Notification, NotificationCreator> createResourceInstance(ChildBroadcastNotifierSource<Notification, NotificationCreator> source)
	{
		return new ChildBroadcastNotifier<Notification, NotificationCreator>(source.getClientPool(), source.getTreeBranchCount(), source.getMessageCreator());
	}
	*/

}
