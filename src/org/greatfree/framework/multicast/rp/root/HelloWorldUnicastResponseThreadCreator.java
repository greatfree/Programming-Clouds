package org.greatfree.framework.multicast.rp.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldUnicastResponse;

// Created: 10/22/2018, Bing Li
public class HelloWorldUnicastResponseThreadCreator implements NotificationQueueCreator<HelloWorldUnicastResponse, HelloWorldUnicastResponseThread>
{

	@Override
	public HelloWorldUnicastResponseThread createInstance(int taskSize)
	{
		return new HelloWorldUnicastResponseThread(taskSize);
	}

}
