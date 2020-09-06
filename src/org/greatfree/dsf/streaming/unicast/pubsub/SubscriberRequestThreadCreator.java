package org.greatfree.dsf.streaming.unicast.pubsub;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.streaming.message.SubscriberRequest;
import org.greatfree.dsf.streaming.message.SubscriberResponse;
import org.greatfree.dsf.streaming.message.SubscriberStream;

// Created: 03/23/2020, Bing Li
class SubscriberRequestThreadCreator implements RequestThreadCreatable<SubscriberRequest, SubscriberStream, SubscriberResponse, SubscriberRequestThread>
{

	@Override
	public SubscriberRequestThread createRequestThreadInstance(int taskSize)
	{
		return new SubscriberRequestThread(taskSize);
	}

}
