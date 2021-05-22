package org.greatfree.framework.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastResponse;

// Created: 08/26/2018, Bing Li
class HelloWorldBroadcastResponseThreadCreator implements NotificationQueueCreator<HelloWorldBroadcastResponse, HelloWorldBroadcastResponseThread>
{

	@Override
	public HelloWorldBroadcastResponseThread createInstance(int taskSize)
	{
		return new HelloWorldBroadcastResponseThread(taskSize);
	}

}
