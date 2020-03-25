package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.front.MaxCachePointingRequest;
import org.greatfree.dip.cps.cache.message.front.MaxCachePointingResponse;
import org.greatfree.dip.cps.cache.message.front.MaxCachePointingStream;

// Created: 07/24/2018, Bing Li
public class MaxCachePointingThreadCreator implements RequestThreadCreatable<MaxCachePointingRequest, MaxCachePointingStream, MaxCachePointingResponse, MaxCachePointingThread>
{

	@Override
	public MaxCachePointingThread createRequestThreadInstance(int taskSize)
	{
		return new MaxCachePointingThread(taskSize);
	}

}
