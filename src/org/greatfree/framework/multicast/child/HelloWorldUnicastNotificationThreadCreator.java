package org.greatfree.framework.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldUnicastNotification;

// Created: 08/26/2018, Bing Li
public class HelloWorldUnicastNotificationThreadCreator implements NotificationQueueCreator<HelloWorldUnicastNotification, HelloWorldUnicastNotificationThread>
{

	@Override
	public HelloWorldUnicastNotificationThread createInstance(int taskSize)
	{
		return new HelloWorldUnicastNotificationThread(taskSize);
	}

}
