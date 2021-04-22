package org.greatfree.framework.multicast.rp.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.HelloWorldUnicastResponse;

// Created: 10/22/2018, Bing Li
public class HelloWorldUnicastResponseThreadCreator implements NotificationThreadCreatable<HelloWorldUnicastResponse, HelloWorldUnicastResponseThread>
{

	@Override
	public HelloWorldUnicastResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldUnicastResponseThread(taskSize);
	}

}
