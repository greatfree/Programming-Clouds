package org.greatfree.framework.old.multicast.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.HelloWorldUnicastNotification;

// Created: 05/19/2017, Bing Li
class HelloWorldUnicastNotificationThreadCreator implements NotificationThreadCreatable<HelloWorldUnicastNotification, HelloWorldUnicastNotificationThread>
{

	@Override
	public HelloWorldUnicastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldUnicastNotificationThread(taskSize);
	}

}
