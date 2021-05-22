package org.greatfree.framework.old.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldAnycastResponse;

// Created: 05/21/2017, Bing Li
class HelloWorldAnycastResponseThreadCreator implements NotificationQueueCreator<HelloWorldAnycastResponse, HelloWorldAnycastResponseThread>
{

	@Override
	public HelloWorldAnycastResponseThread createInstance(int taskSize)
	{
		return new HelloWorldAnycastResponseThread(taskSize);
	}

}
