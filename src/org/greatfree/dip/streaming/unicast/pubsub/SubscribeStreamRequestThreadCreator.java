package org.greatfree.dip.streaming.unicast.pubsub;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.streaming.message.SubscribeOutStream;
import org.greatfree.dip.streaming.message.SubscribeStreamRequest;
import org.greatfree.dip.streaming.message.SubscribeStreamResponse;

// Created: 03/23/2020, Bing Li
class SubscribeStreamRequestThreadCreator implements RequestThreadCreatable<SubscribeStreamRequest, SubscribeOutStream, SubscribeStreamResponse, SubscribeStreamRequestThread>
{

	@Override
	public SubscribeStreamRequestThread createRequestThreadInstance(int taskSize)
	{
		return new SubscribeStreamRequestThread(taskSize);
	}

}
