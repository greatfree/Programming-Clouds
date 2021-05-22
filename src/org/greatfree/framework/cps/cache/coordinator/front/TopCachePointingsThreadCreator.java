package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.TopCachePointingsRequest;
import org.greatfree.framework.cps.cache.message.front.TopCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.front.TopCachePointingsStream;

// Created: 07/24/2018, Bing Li
public class TopCachePointingsThreadCreator implements RequestQueueCreator<TopCachePointingsRequest, TopCachePointingsStream, TopCachePointingsResponse, TopCachePointingsThread>
{

	@Override
	public TopCachePointingsThread createInstance(int taskSize)
	{
		return new TopCachePointingsThread(taskSize);
	}

}
