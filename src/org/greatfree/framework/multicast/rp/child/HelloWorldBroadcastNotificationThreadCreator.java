package org.greatfree.framework.multicast.rp.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastNotification;

// Created: 10/22/2018, Bing Li
public class HelloWorldBroadcastNotificationThreadCreator implements NotificationThreadCreatable<HelloWorldBroadcastNotification, HelloWorldBroadcastNotificationThread>
{

	@Override
	public HelloWorldBroadcastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldBroadcastNotificationThread(taskSize);
	}

}
