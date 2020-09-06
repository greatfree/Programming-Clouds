package org.greatfree.dsf.multicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.multicast.message.HelloWorldBroadcastResponse;

// Created: 08/26/2018, Bing Li
class HelloWorldBroadcastResponseThreadCreator implements NotificationThreadCreatable<HelloWorldBroadcastResponse, HelloWorldBroadcastResponseThread>
{

	@Override
	public HelloWorldBroadcastResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldBroadcastResponseThread(taskSize);
	}

}
