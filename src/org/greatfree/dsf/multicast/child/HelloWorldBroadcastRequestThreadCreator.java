package org.greatfree.dsf.multicast.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.multicast.message.HelloWorldBroadcastRequest;

// Created: 09/10/2018, Bing Li
class HelloWorldBroadcastRequestThreadCreator implements NotificationThreadCreatable<HelloWorldBroadcastRequest, HelloWorldBroadcastRequestThread>
{

	@Override
	public HelloWorldBroadcastRequestThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldBroadcastRequestThread(taskSize);
	}

}
