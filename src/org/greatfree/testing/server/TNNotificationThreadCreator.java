package org.greatfree.testing.server;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.testing.message.TNNotification;

// Created: 04/10/2020, Bing Li
class TNNotificationThreadCreator implements NotificationThreadCreatable<TNNotification, TNNotificationThread>
{

	@Override
	public TNNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new TNNotificationThread(taskSize);
	}

}
