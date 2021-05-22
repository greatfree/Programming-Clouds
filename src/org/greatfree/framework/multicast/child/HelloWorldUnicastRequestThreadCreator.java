package org.greatfree.framework.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldUnicastRequest;

// Created: 08/26/2018, Bing Li
public class HelloWorldUnicastRequestThreadCreator implements NotificationQueueCreator<HelloWorldUnicastRequest, HelloWorldUnicastRequestThread>
{

	@Override
	public HelloWorldUnicastRequestThread createInstance(int taskSize)
	{
		return new HelloWorldUnicastRequestThread(taskSize);
	}

}
