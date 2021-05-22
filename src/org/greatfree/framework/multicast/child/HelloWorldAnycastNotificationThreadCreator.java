package org.greatfree.framework.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldAnycastNotification;

// Created: 08/26/2018, Bing Li
public class HelloWorldAnycastNotificationThreadCreator implements NotificationQueueCreator<HelloWorldAnycastNotification, HelloWorldAnycastNotificationThread>
{

	@Override
	public HelloWorldAnycastNotificationThread createInstance(int taskSize)
	{
		return new HelloWorldAnycastNotificationThread(taskSize);
	}

}
