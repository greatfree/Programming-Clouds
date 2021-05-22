package org.greatfree.framework.multicast.rp.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldAnycastNotification;

// Created: 10/22/2018, Bing Li
public class HelloWorldAnycastNotificationThreadCreator implements NotificationQueueCreator<HelloWorldAnycastNotification, HelloWorldAnycastNotificationThread>
{

	@Override
	public HelloWorldAnycastNotificationThread createInstance(int taskSize)
	{
		return new HelloWorldAnycastNotificationThread(taskSize);
	}

}
