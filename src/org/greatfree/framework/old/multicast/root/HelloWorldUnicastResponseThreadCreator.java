package org.greatfree.framework.old.multicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.HelloWorldUnicastResponse;

// Created: 05/21/2017, Bing Li
class HelloWorldUnicastResponseThreadCreator implements NotificationThreadCreatable<HelloWorldUnicastResponse, HelloWorldUnicastResponseThread>
{

	@Override
	public HelloWorldUnicastResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldUnicastResponseThread(taskSize);
	}

}
