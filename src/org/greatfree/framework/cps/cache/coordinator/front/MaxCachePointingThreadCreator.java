package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.MaxCachePointingRequest;
import org.greatfree.framework.cps.cache.message.front.MaxCachePointingResponse;
import org.greatfree.framework.cps.cache.message.front.MaxCachePointingStream;

// Created: 07/24/2018, Bing Li
public class MaxCachePointingThreadCreator implements RequestQueueCreator<MaxCachePointingRequest, MaxCachePointingStream, MaxCachePointingResponse, MaxCachePointingThread>
{

	@Override
	public MaxCachePointingThread createInstance(int taskSize)
	{
		return new MaxCachePointingThread(taskSize);
	}

}
