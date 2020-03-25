package org.greatfree.dip.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.streaming.message.SubscribeOutStream;
import org.greatfree.dip.streaming.message.SubscribeStreamRequest;
import org.greatfree.dip.streaming.message.SubscribeStreamResponse;

// Created: 03/19/2020, Bing Li
class SubscribeStreamRequestThreadCreator implements RequestThreadCreatable<SubscribeStreamRequest, SubscribeOutStream, SubscribeStreamResponse, SubscribeStreamRequestThread>
{

	@Override
	public SubscribeStreamRequestThread createRequestThreadInstance(int taskSize)
	{
		return new SubscribeStreamRequestThread(taskSize);
	}

}
