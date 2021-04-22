package org.greatfree.framework.streaming.broadcast.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.ShutdownChildrenBroadcastNotification;

// Created: 03/19/2020, Bing Li
class ShutdownChildrenBroadcastNotificationThreadCreator implements NotificationThreadCreatable<ShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread>
{

	@Override
	public ShutdownChildrenBroadcastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownChildrenBroadcastNotificationThread(taskSize);
	}

}
