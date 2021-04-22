package org.greatfree.framework.multicast.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.ShutdownChildrenBroadcastNotification;

// Created: 09/10/2018, Bing Li
class ShutdownChildrenBroadcastNotificationThreadCreator implements NotificationThreadCreatable<ShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread>
{

	@Override
	public ShutdownChildrenBroadcastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownChildrenBroadcastNotificationThread(taskSize);
	}

}
