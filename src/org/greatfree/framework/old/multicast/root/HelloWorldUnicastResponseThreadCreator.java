package org.greatfree.framework.old.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldUnicastResponse;

// Created: 05/21/2017, Bing Li
class HelloWorldUnicastResponseThreadCreator implements NotificationQueueCreator<HelloWorldUnicastResponse, HelloWorldUnicastResponseThread>
{

	@Override
	public HelloWorldUnicastResponseThread createInstance(int taskSize)
	{
		return new HelloWorldUnicastResponseThread(taskSize);
	}

}
