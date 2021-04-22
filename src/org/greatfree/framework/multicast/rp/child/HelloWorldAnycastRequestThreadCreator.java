package org.greatfree.framework.multicast.rp.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.rp.message.HelloWorldAnycastRequest;

// Created: 10/22/2018, Bing Li
public class HelloWorldAnycastRequestThreadCreator implements NotificationThreadCreatable<HelloWorldAnycastRequest, HelloWorldAnycastRequestThread>
{

	@Override
	public HelloWorldAnycastRequestThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldAnycastRequestThread(taskSize);
	}

}
