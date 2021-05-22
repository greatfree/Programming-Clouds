package org.greatfree.framework.multicast.rp.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldAnycastResponse;

// Created: 10/22/2018, Bing Li
public class HelloWorldAnycastResponseThreadCreator implements NotificationQueueCreator<HelloWorldAnycastResponse, HelloWorldAnycastResponseThread>
{

	@Override
	public HelloWorldAnycastResponseThread createInstance(int taskSize)
	{
		return new HelloWorldAnycastResponseThread(taskSize);
	}

}
