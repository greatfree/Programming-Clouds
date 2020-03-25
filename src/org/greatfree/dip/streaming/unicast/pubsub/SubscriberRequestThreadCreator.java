package org.greatfree.dip.streaming.unicast.pubsub;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.streaming.message.SubscriberRequest;
import org.greatfree.dip.streaming.message.SubscriberResponse;
import org.greatfree.dip.streaming.message.SubscriberStream;

// Created: 03/23/2020, Bing Li
class SubscriberRequestThreadCreator implements RequestThreadCreatable<SubscriberRequest, SubscriberStream, SubscriberResponse, SubscriberRequestThread>
{

	@Override
	public SubscriberRequestThread createRequestThreadInstance(int taskSize)
	{
		return new SubscriberRequestThread(taskSize);
	}

}
