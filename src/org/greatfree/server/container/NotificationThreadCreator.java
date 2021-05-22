package org.greatfree.server.container;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.container.Notification;

// Created: 12/18/2018, Bing Li
class NotificationThreadCreator implements NotificationQueueCreator<Notification, NotificationThread>
{

	@Override
	public NotificationThread createInstance(int taskSize)
	{
		return new NotificationThread(taskSize);
	}

}
