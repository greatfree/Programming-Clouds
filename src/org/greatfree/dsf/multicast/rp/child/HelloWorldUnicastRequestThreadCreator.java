package org.greatfree.dsf.multicast.rp.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.multicast.rp.message.HelloWorldUnicastRequest;

// Created: 10/22/2018, Bing Li
public class HelloWorldUnicastRequestThreadCreator implements NotificationThreadCreatable<HelloWorldUnicastRequest, HelloWorldUnicastRequestThread>
{

	@Override
	public HelloWorldUnicastRequestThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldUnicastRequestThread(taskSize);
	}

}
