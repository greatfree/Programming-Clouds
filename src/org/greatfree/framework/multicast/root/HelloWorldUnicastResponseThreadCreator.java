package org.greatfree.framework.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldUnicastResponse;

// Created: 08/26/2018, Bing Li
class HelloWorldUnicastResponseThreadCreator implements NotificationQueueCreator<HelloWorldUnicastResponse, HelloWorldUnicastResponseThread>
{

	@Override
	public HelloWorldUnicastResponseThread createInstance(int taskSize)
	{
		return new HelloWorldUnicastResponseThread(taskSize);
	}

}
