package org.greatfree.framework.multicast.bound.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.ShutdownChildrenBroadcastNotification;

// Created: 08/26/2018, Bing Li
public class ShutdownChildrenBroadcastNotificationThreadCreator implements NotificationQueueCreator<ShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread>
{
	@Override
	public ShutdownChildrenBroadcastNotificationThread createInstance(int taskSize)
	{
		return new ShutdownChildrenBroadcastNotificationThread(taskSize);
	}

}
