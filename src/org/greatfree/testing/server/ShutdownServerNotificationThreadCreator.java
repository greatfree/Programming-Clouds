package org.greatfree.testing.server;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 04/10/2020, Bing Li
class ShutdownServerNotificationThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownServerNotificationThread>
{

	@Override
	public ShutdownServerNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownServerNotificationThread(taskSize);
	}

}
