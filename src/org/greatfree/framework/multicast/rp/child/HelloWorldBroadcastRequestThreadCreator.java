package org.greatfree.framework.multicast.rp.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.rp.message.HelloWorldBroadcastRequest;

// Created: 10/22/2018, Bing Li
public class HelloWorldBroadcastRequestThreadCreator implements NotificationQueueCreator<HelloWorldBroadcastRequest, HelloWorldBroadcastRequestThread>
{

	@Override
	public HelloWorldBroadcastRequestThread createInstance(int taskSize)
	{
		return new HelloWorldBroadcastRequestThread(taskSize);
	}

}
