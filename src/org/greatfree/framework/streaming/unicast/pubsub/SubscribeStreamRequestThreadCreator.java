package org.greatfree.framework.streaming.unicast.pubsub;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.streaming.message.SubscribeOutStream;
import org.greatfree.framework.streaming.message.SubscribeStreamRequest;
import org.greatfree.framework.streaming.message.SubscribeStreamResponse;

// Created: 03/23/2020, Bing Li
class SubscribeStreamRequestThreadCreator implements RequestQueueCreator<SubscribeStreamRequest, SubscribeOutStream, SubscribeStreamResponse, SubscribeStreamRequestThread>
{

	@Override
	public SubscribeStreamRequestThread createInstance(int taskSize)
	{
		return new SubscribeStreamRequestThread(taskSize);
	}

}
