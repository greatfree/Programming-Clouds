package org.greatfree.dip.multicast.bound.child;

import org.greatfree.concurrency.reactive.BoundRequestThreadCreatable;
import org.greatfree.dip.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.dip.multicast.message.MessageDisposer;
import org.greatfree.dip.multicast.message.OldHelloWorldBroadcastRequest;

// Created: 08/26/2018, Bing Li
public class HelloWorldBroadcastRequestThreadCreator implements BoundRequestThreadCreatable<OldHelloWorldBroadcastRequest, HelloWorldBroadcastResponse, MessageDisposer<OldHelloWorldBroadcastRequest>, HelloWorldBroadcastRequestThread>
{

	@Override
	public HelloWorldBroadcastRequestThread createRequestThreadInstance(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastRequest> reqBinder)
	{
		return new HelloWorldBroadcastRequestThread(taskSize, dispatcherKey, reqBinder);
	}

}
