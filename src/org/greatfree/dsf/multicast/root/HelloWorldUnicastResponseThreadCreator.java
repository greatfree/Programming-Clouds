package org.greatfree.dsf.multicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.multicast.message.HelloWorldUnicastResponse;

// Created: 08/26/2018, Bing Li
class HelloWorldUnicastResponseThreadCreator implements NotificationThreadCreatable<HelloWorldUnicastResponse, HelloWorldUnicastResponseThread>
{

	@Override
	public HelloWorldUnicastResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldUnicastResponseThread(taskSize);
	}

}
