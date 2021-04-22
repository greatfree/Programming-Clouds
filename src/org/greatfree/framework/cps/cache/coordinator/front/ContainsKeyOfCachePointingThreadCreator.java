package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.front.ContainsKeyOfCachePointingRequest;
import org.greatfree.framework.cps.cache.message.front.ContainsKeyOfCachePointingResponse;
import org.greatfree.framework.cps.cache.message.front.ContainsKeyOfCachePointingStream;

// Created: 07/24/2018, Bing Li
public class ContainsKeyOfCachePointingThreadCreator implements RequestThreadCreatable<ContainsKeyOfCachePointingRequest, ContainsKeyOfCachePointingStream, ContainsKeyOfCachePointingResponse, ContainsKeyOfCachePointingThread>
{

	@Override
	public ContainsKeyOfCachePointingThread createRequestThreadInstance(int taskSize)
	{
		return new ContainsKeyOfCachePointingThread(taskSize);
	}

}
