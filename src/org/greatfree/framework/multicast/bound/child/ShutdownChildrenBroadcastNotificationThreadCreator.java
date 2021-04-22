package org.greatfree.framework.multicast.bound.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.ShutdownChildrenBroadcastNotification;

// Created: 08/26/2018, Bing Li
public class ShutdownChildrenBroadcastNotificationThreadCreator implements NotificationThreadCreatable<ShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread>
{
	@Override
	public ShutdownChildrenBroadcastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownChildrenBroadcastNotificationThread(taskSize);
	}

}
