package org.greatfree.dsf.multicast.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.multicast.message.HelloWorldAnycastRequest;

// Created: 08/26/2018, Bing Li
public class HelloWorldAnycastRequestThreadCreator implements NotificationThreadCreatable<HelloWorldAnycastRequest, HelloWorldAnycastRequestThread>
{

	@Override
	public HelloWorldAnycastRequestThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldAnycastRequestThread(taskSize);
	}

}
