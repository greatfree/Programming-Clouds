package org.greatfree.dsf.multicast.bound.child;

import org.greatfree.concurrency.reactive.BoundRequestThreadCreatable;
import org.greatfree.dsf.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.dsf.multicast.message.MessageDisposer;
import org.greatfree.dsf.multicast.message.OldHelloWorldBroadcastRequest;

// Created: 08/26/2018, Bing Li
public class HelloWorldBroadcastRequestThreadCreator implements BoundRequestThreadCreatable<OldHelloWorldBroadcastRequest, HelloWorldBroadcastResponse, MessageDisposer<OldHelloWorldBroadcastRequest>, HelloWorldBroadcastRequestThread>
{

	@Override
	public HelloWorldBroadcastRequestThread createRequestThreadInstance(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastRequest> reqBinder)
	{
		return new HelloWorldBroadcastRequestThread(taskSize, dispatcherKey, reqBinder);
	}

}
