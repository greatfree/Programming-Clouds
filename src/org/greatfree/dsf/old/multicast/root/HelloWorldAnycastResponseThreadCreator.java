package org.greatfree.dsf.old.multicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.multicast.message.HelloWorldAnycastResponse;

// Created: 05/21/2017, Bing Li
class HelloWorldAnycastResponseThreadCreator implements NotificationThreadCreatable<HelloWorldAnycastResponse, HelloWorldAnycastResponseThread>
{

	@Override
	public HelloWorldAnycastResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldAnycastResponseThread(taskSize);
	}

}
