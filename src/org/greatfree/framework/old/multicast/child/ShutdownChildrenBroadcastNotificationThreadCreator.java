package org.greatfree.framework.old.multicast.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.OldShutdownChildrenBroadcastNotification;

/*
 * This is an implementation of the interface BoundNotificationThreadCreatable to create the instance of BroadcastNotificationThread inside the pool, BoundNotificationDispatcher. 11/26/2014, Bing Li
 */

// Created: 05/19/2017, Bing Li
//public class ShutdownChildrenBroadcastNotificationThreadCreator implements BoundNotificationThreadCreatable<ShutdownChildrenBroadcastNotification, ClusterMessageDisposer<ShutdownChildrenBroadcastNotification>, ShutdownChildrenBroadcastNotificationThread>
class ShutdownChildrenBroadcastNotificationThreadCreator implements NotificationThreadCreatable<OldShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread>
{

	@Override
	public ShutdownChildrenBroadcastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownChildrenBroadcastNotificationThread(taskSize);
	}

	/*
	@Override
	public ShutdownChildrenBroadcastNotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, ClusterMessageDisposer<ShutdownChildrenBroadcastNotification> binder)
	{
		return new ShutdownChildrenBroadcastNotificationThread(taskSize, dispatcherKey, binder);
	}
	*/

}
