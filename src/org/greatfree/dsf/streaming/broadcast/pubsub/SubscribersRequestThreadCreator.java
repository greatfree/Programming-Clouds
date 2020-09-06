package org.greatfree.dsf.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.streaming.message.SubscribersRequest;
import org.greatfree.dsf.streaming.message.SubscribersResponse;
import org.greatfree.dsf.streaming.message.SubscribersStream;

// Created: 03/20/2020, Bing Li
class SubscribersRequestThreadCreator implements RequestThreadCreatable<SubscribersRequest, SubscribersStream, SubscribersResponse, SubscribersRequestThread>
{

	@Override
	public SubscribersRequestThread createRequestThreadInstance(int taskSize)
	{
		return new SubscribersRequestThread(taskSize);
	}

}
