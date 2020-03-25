package org.greatfree.dip.multicast.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dip.multicast.message.HelloWorldUnicastRequest;

// Created: 08/26/2018, Bing Li
public class HelloWorldUnicastRequestThreadCreator implements NotificationThreadCreatable<HelloWorldUnicastRequest, HelloWorldUnicastRequestThread>
{

	@Override
	public HelloWorldUnicastRequestThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldUnicastRequestThread(taskSize);
	}

}
