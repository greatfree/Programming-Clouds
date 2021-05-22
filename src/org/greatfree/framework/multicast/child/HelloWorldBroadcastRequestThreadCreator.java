package org.greatfree.framework.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastRequest;

// Created: 09/10/2018, Bing Li
class HelloWorldBroadcastRequestThreadCreator implements NotificationQueueCreator<HelloWorldBroadcastRequest, HelloWorldBroadcastRequestThread>
{

	@Override
	public HelloWorldBroadcastRequestThread createInstance(int taskSize)
	{
		return new HelloWorldBroadcastRequestThread(taskSize);
	}

}
