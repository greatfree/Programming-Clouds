package org.greatfree.framework.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.streaming.message.SubscribersRequest;
import org.greatfree.framework.streaming.message.SubscribersResponse;
import org.greatfree.framework.streaming.message.SubscribersStream;

// Created: 03/20/2020, Bing Li
class SubscribersRequestThreadCreator implements RequestQueueCreator<SubscribersRequest, SubscribersStream, SubscribersResponse, SubscribersRequestThread>
{

	@Override
	public SubscribersRequestThread createInstance(int taskSize)
	{
		return new SubscribersRequestThread(taskSize);
	}

}
