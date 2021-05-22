package org.greatfree.framework.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastNotification;

// Created: 09/10/2018, Bing Li
class HelloWorldBroadcastNotificationThreadCreator implements NotificationQueueCreator<HelloWorldBroadcastNotification, HelloWorldBroadcastNotificationThread>
{

	@Override
	public HelloWorldBroadcastNotificationThread createInstance(int taskSize)
	{
		return new HelloWorldBroadcastNotificationThread(taskSize);
	}

}
