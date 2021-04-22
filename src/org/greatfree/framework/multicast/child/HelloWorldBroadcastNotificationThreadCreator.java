package org.greatfree.framework.multicast.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastNotification;

// Created: 09/10/2018, Bing Li
class HelloWorldBroadcastNotificationThreadCreator implements NotificationThreadCreatable<HelloWorldBroadcastNotification, HelloWorldBroadcastNotificationThread>
{

	@Override
	public HelloWorldBroadcastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldBroadcastNotificationThread(taskSize);
	}

}
