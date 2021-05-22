package org.greatfree.framework.streaming.broadcast.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.ShutdownChildrenAdminNotification;

// Created: 03/19/2020, Bing Li
class ShutdownChildrenAdminNotificationThreadCreator implements NotificationQueueCreator<ShutdownChildrenAdminNotification, ShutdownChildrenAdminNotificationThread>
{

	@Override
	public ShutdownChildrenAdminNotificationThread createInstance(int taskSize)
	{
		return new ShutdownChildrenAdminNotificationThread(taskSize);
	}

}
