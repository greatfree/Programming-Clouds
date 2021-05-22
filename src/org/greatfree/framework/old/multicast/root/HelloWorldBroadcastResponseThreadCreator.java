package org.greatfree.framework.old.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastResponse;

// Created: 05/21/2017, Bing Li
class HelloWorldBroadcastResponseThreadCreator implements NotificationQueueCreator<HelloWorldBroadcastResponse, HelloWorldBroadcastResponseThread>
{

	@Override
	public HelloWorldBroadcastResponseThread createInstance(int taskSize)
	{
		return new HelloWorldBroadcastResponseThread(taskSize);
	}

}
