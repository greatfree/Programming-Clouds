package org.greatfree.app.p2p;

import org.greatfree.app.p2p.message.HelloNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 08/19/2018, Bing Li
class HelloNotificationThreadCreator implements NotificationQueueCreator<HelloNotification, HelloNotificationThread>
{

	@Override
	public HelloNotificationThread createInstance(int taskSize)
	{
		return new HelloNotificationThread(taskSize);
	}

}
