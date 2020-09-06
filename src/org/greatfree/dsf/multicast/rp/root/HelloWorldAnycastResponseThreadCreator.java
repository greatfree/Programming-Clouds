package org.greatfree.dsf.multicast.rp.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.multicast.message.HelloWorldAnycastResponse;

// Created: 10/22/2018, Bing Li
public class HelloWorldAnycastResponseThreadCreator implements NotificationThreadCreatable<HelloWorldAnycastResponse, HelloWorldAnycastResponseThread>
{

	@Override
	public HelloWorldAnycastResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldAnycastResponseThread(taskSize);
	}

}
