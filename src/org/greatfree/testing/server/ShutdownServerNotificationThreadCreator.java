package org.greatfree.testing.server;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 04/10/2020, Bing Li
class ShutdownServerNotificationThreadCreator implements NotificationQueueCreator<ShutdownServerNotification, ShutdownServerNotificationThread>
{

	@Override
	public ShutdownServerNotificationThread createInstance(int taskSize)
	{
		return new ShutdownServerNotificationThread(taskSize);
	}

}
