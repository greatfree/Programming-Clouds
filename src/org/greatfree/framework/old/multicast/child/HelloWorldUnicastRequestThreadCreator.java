package org.greatfree.framework.old.multicast.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.HelloWorldUnicastRequest;

/*
 * The HelloWorldUnicastRequestThread creator generates such a thread when the current alive threads are busy. 05/21/2017, Bing Li
 */

// Created: 05/21/2017, Bing Li
class HelloWorldUnicastRequestThreadCreator implements NotificationThreadCreatable<HelloWorldUnicastRequest, HelloWorldUnicastRequestThread>
{

	@Override
	public HelloWorldUnicastRequestThread createNotificationThreadInstance(int taskSize)
	{
		return new HelloWorldUnicastRequestThread(taskSize);
	}

}
