package org.greatfree.framework.multicast.rp.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.rp.message.HelloWorldAnycastRequest;

// Created: 10/22/2018, Bing Li
public class HelloWorldAnycastRequestThreadCreator implements NotificationQueueCreator<HelloWorldAnycastRequest, HelloWorldAnycastRequestThread>
{

	@Override
	public HelloWorldAnycastRequestThread createInstance(int taskSize)
	{
		return new HelloWorldAnycastRequestThread(taskSize);
	}

}
