package org.greatfree.framework.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldAnycastRequest;

// Created: 08/26/2018, Bing Li
public class HelloWorldAnycastRequestThreadCreator implements NotificationQueueCreator<HelloWorldAnycastRequest, HelloWorldAnycastRequestThread>
{

	@Override
	public HelloWorldAnycastRequestThread createInstance(int taskSize)
	{
		return new HelloWorldAnycastRequestThread(taskSize);
	}

}
