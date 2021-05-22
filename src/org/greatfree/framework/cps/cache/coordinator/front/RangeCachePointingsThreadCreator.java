package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.RangeCachePointingsRequest;
import org.greatfree.framework.cps.cache.message.front.RangeCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.front.RangeCachePointingsStream;

// Created: 07/24/2018, Bing Li
public class RangeCachePointingsThreadCreator implements RequestQueueCreator<RangeCachePointingsRequest, RangeCachePointingsStream, RangeCachePointingsResponse, RangeCachePointingsThread>
{

	@Override
	public RangeCachePointingsThread createInstance(int taskSize)
	{
		return new RangeCachePointingsThread(taskSize);
	}

}
