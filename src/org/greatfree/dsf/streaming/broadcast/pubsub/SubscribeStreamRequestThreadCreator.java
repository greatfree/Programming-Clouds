package org.greatfree.dsf.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.streaming.message.SubscribeOutStream;
import org.greatfree.dsf.streaming.message.SubscribeStreamRequest;
import org.greatfree.dsf.streaming.message.SubscribeStreamResponse;

// Created: 03/19/2020, Bing Li
class SubscribeStreamRequestThreadCreator implements RequestThreadCreatable<SubscribeStreamRequest, SubscribeOutStream, SubscribeStreamResponse, SubscribeStreamRequestThread>
{

	@Override
	public SubscribeStreamRequestThread createRequestThreadInstance(int taskSize)
	{
		return new SubscribeStreamRequestThread(taskSize);
	}

}
