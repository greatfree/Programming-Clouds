package org.greatfree.framework.multicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.HelloWorldAnycastResponse;

// Created: 08/26/2018, Bing Li
class HelloWorldAnycastResponseThreadCreator implements NotificationThreadCreatable<HelloWorldAnycastResponse, HelloWorldAnycastResponseThread>
{

	@Override
	public HelloWorldAnycastResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldAnycastResponseThread(taskSize);
	}

}
