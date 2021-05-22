package org.greatfree.framework.multicast.rp.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastNotification;

// Created: 10/22/2018, Bing Li
public class HelloWorldBroadcastNotificationThreadCreator implements NotificationQueueCreator<HelloWorldBroadcastNotification, HelloWorldBroadcastNotificationThread>
{

	@Override
	public HelloWorldBroadcastNotificationThread createInstance(int taskSize)
	{
		return new HelloWorldBroadcastNotificationThread(taskSize);
	}

}
