package org.greatfree.framework.old.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldAnycastRequest;

/*
 * The HelloWorldAnycastNotificationThread creator generates such a thread when the current alive threads are busy. 05/21/2017, Bing Li
 */

// Created: 05/21/2017, Bing Li
class HelloWorldAnycastRequestThreadCreator implements NotificationQueueCreator<HelloWorldAnycastRequest, HelloWorldAnycastRequestThread>
{

	@Override
	public HelloWorldAnycastRequestThread createInstance(int taskSize)
	{
		return new HelloWorldAnycastRequestThread(taskSize);
	}

}
