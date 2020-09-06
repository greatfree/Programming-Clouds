package org.greatfree.dsf.old.multicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.multicast.message.HelloWorldBroadcastResponse;

// Created: 05/21/2017, Bing Li
class HelloWorldBroadcastResponseThreadCreator implements NotificationThreadCreatable<HelloWorldBroadcastResponse, HelloWorldBroadcastResponseThread>
{

	@Override
	public HelloWorldBroadcastResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldBroadcastResponseThread(taskSize);
	}

}
