package org.greatfree.framework.old.multicast.child;

import org.greatfree.concurrency.reactive.BoundRequestThreadCreatable;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastRequest;
import org.greatfree.message.MessageDisposer;

/*
 * The creator is responsible for initializing an instance of HelloWorldBroadcastRequestThread. It works with the BoundBroadcastRequestDispatcher to manage the searching process efficiently and concurrently. 11/29/2014, Bing Li
 */

// Created: 05/20/2017, Bing Li
class HelloWorldBroadcastRequestThreadCreator implements BoundRequestThreadCreatable<OldHelloWorldBroadcastRequest, HelloWorldBroadcastResponse, MessageDisposer<OldHelloWorldBroadcastRequest>, HelloWorldBroadcastRequestThread>
{

	@Override
	public HelloWorldBroadcastRequestThread createRequestThreadInstance(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastRequest> reqBinder)
	{
		return new HelloWorldBroadcastRequestThread(taskSize, dispatcherKey, reqBinder);
	}

}
