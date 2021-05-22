package org.greatfree.framework.old.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldUnicastNotification;

// Created: 05/19/2017, Bing Li
class HelloWorldUnicastNotificationThreadCreator implements NotificationQueueCreator<HelloWorldUnicastNotification, HelloWorldUnicastNotificationThread>
{

	@Override
	public HelloWorldUnicastNotificationThread createInstance(int taskSize)
	{
		return new HelloWorldUnicastNotificationThread(taskSize);
	}

}
