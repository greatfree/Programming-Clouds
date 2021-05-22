package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.ContainsKeyOfCachePointingRequest;
import org.greatfree.framework.cps.cache.message.front.ContainsKeyOfCachePointingResponse;
import org.greatfree.framework.cps.cache.message.front.ContainsKeyOfCachePointingStream;

// Created: 07/24/2018, Bing Li
public class ContainsKeyOfCachePointingThreadCreator implements RequestQueueCreator<ContainsKeyOfCachePointingRequest, ContainsKeyOfCachePointingStream, ContainsKeyOfCachePointingResponse, ContainsKeyOfCachePointingThread>
{

	@Override
	public ContainsKeyOfCachePointingThread createInstance(int taskSize)
	{
		return new ContainsKeyOfCachePointingThread(taskSize);
	}

}
