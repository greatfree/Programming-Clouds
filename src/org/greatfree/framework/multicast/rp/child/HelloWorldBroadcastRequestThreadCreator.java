package org.greatfree.framework.multicast.rp.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.rp.message.HelloWorldBroadcastRequest;

// Created: 10/22/2018, Bing Li
public class HelloWorldBroadcastRequestThreadCreator implements NotificationThreadCreatable<HelloWorldBroadcastRequest, HelloWorldBroadcastRequestThread>
{

	@Override
	public HelloWorldBroadcastRequestThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldBroadcastRequestThread(taskSize);
	}

}
