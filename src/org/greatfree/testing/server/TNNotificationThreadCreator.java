package org.greatfree.testing.server;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.TNNotification;

// Created: 04/10/2020, Bing Li
class TNNotificationThreadCreator implements NotificationQueueCreator<TNNotification, TNNotificationThread>
{

	@Override
	public TNNotificationThread createInstance(int taskSize)
	{
		return new TNNotificationThread(taskSize);
	}

}
