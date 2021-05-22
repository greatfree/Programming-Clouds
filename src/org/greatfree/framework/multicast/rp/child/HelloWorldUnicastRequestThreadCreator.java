package org.greatfree.framework.multicast.rp.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.rp.message.HelloWorldUnicastRequest;

// Created: 10/22/2018, Bing Li
public class HelloWorldUnicastRequestThreadCreator implements NotificationQueueCreator<HelloWorldUnicastRequest, HelloWorldUnicastRequestThread>
{

	@Override
	public HelloWorldUnicastRequestThread createInstance(int taskSize)
	{
		return new HelloWorldUnicastRequestThread(taskSize);
	}

}
