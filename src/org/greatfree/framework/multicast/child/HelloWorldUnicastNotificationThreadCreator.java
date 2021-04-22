package org.greatfree.framework.multicast.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.HelloWorldUnicastNotification;

// Created: 08/26/2018, Bing Li
public class HelloWorldUnicastNotificationThreadCreator implements NotificationThreadCreatable<HelloWorldUnicastNotification, HelloWorldUnicastNotificationThread>
{

	@Override
	public HelloWorldUnicastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldUnicastNotificationThread(taskSize);
	}

}
