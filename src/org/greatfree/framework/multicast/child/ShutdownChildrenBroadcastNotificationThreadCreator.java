package org.greatfree.framework.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.ShutdownChildrenBroadcastNotification;

// Created: 09/10/2018, Bing Li
class ShutdownChildrenBroadcastNotificationThreadCreator implements NotificationQueueCreator<ShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread>
{

	@Override
	public ShutdownChildrenBroadcastNotificationThread createInstance(int taskSize)
	{
		return new ShutdownChildrenBroadcastNotificationThread(taskSize);
	}

}
