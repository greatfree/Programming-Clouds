package org.greatfree.dsf.multicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.multicast.message.HelloWorldAnycastResponse;

// Created: 08/26/2018, Bing Li
class HelloWorldAnycastResponseThreadCreator implements NotificationThreadCreatable<HelloWorldAnycastResponse, HelloWorldAnycastResponseThread>
{

	@Override
	public HelloWorldAnycastResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldAnycastResponseThread(taskSize);
	}

}
