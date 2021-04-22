package org.greatfree.framework.multicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.HelloWorldUnicastResponse;

// Created: 08/26/2018, Bing Li
class HelloWorldUnicastResponseThreadCreator implements NotificationThreadCreatable<HelloWorldUnicastResponse, HelloWorldUnicastResponseThread>
{

	@Override
	public HelloWorldUnicastResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldUnicastResponseThread(taskSize);
	}

}
