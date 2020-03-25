package org.greatfree.app.p2p;

import org.greatfree.app.p2p.message.HelloNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 08/19/2018, Bing Li
class HelloNotificationThreadCreator implements NotificationThreadCreatable<HelloNotification, HelloNotificationThread>
{

	@Override
	public HelloNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloNotificationThread(taskSize);
	}

}
