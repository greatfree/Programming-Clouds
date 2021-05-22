package org.greatfree.framework.old.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldAnycastNotification;

/*
 * The HelloWorldAnycastNotificationThread creator generates such a thread when the current alive threads are busy. 05/21/2017, Bing Li
 */

// Created: 05/19/2017, Bing Li
class HelloWorldAnycastNotificationThreadCreator implements NotificationQueueCreator<HelloWorldAnycastNotification, HelloWorldAnycastNotificationThread>
{

	@Override
	public HelloWorldAnycastNotificationThread createInstance(int taskSize)
	{
		return new HelloWorldAnycastNotificationThread(taskSize);
	}

}
