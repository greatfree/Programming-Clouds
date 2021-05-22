package org.greatfree.framework.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldAnycastResponse;

// Created: 08/26/2018, Bing Li
class HelloWorldAnycastResponseThreadCreator implements NotificationQueueCreator<HelloWorldAnycastResponse, HelloWorldAnycastResponseThread>
{

	@Override
	public HelloWorldAnycastResponseThread createInstance(int taskSize)
	{
		return new HelloWorldAnycastResponseThread(taskSize);
	}

}
