package org.greatfree.server.container;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.message.container.Notification;

// Created: 12/18/2018, Bing Li
class NotificationThreadCreator implements NotificationThreadCreatable<Notification, NotificationThread>
{

	@Override
	public NotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new NotificationThread(taskSize);
	}

}
