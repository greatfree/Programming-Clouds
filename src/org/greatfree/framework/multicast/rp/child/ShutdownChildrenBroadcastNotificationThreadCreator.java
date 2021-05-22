package org.greatfree.framework.multicast.rp.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.ShutdownChildrenBroadcastNotification;

// Created: 10/22/2018, Bing Li
public class ShutdownChildrenBroadcastNotificationThreadCreator implements NotificationQueueCreator<ShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread>
{

	@Override
	public ShutdownChildrenBroadcastNotificationThread createInstance(int taskSize)
	{
		return new ShutdownChildrenBroadcastNotificationThread(taskSize);
	}

}
