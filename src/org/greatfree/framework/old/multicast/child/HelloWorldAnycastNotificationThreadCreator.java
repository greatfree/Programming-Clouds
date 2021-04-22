package org.greatfree.framework.old.multicast.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.HelloWorldAnycastNotification;

/*
 * The HelloWorldAnycastNotificationThread creator generates such a thread when the current alive threads are busy. 05/21/2017, Bing Li
 */

// Created: 05/19/2017, Bing Li
class HelloWorldAnycastNotificationThreadCreator implements NotificationThreadCreatable<HelloWorldAnycastNotification, HelloWorldAnycastNotificationThread>
{

	@Override
	public HelloWorldAnycastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldAnycastNotificationThread(taskSize);
	}

}
