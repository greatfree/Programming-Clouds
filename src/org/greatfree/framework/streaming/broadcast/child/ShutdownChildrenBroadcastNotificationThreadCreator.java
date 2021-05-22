package org.greatfree.framework.streaming.broadcast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.ShutdownChildrenBroadcastNotification;

// Created: 03/19/2020, Bing Li
class ShutdownChildrenBroadcastNotificationThreadCreator implements NotificationQueueCreator<ShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread>
{

	@Override
	public ShutdownChildrenBroadcastNotificationThread createInstance(int taskSize)
	{
		return new ShutdownChildrenBroadcastNotificationThread(taskSize);
	}

}
