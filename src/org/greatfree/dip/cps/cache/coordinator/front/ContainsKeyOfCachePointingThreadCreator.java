package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.front.ContainsKeyOfCachePointingRequest;
import org.greatfree.dip.cps.cache.message.front.ContainsKeyOfCachePointingResponse;
import org.greatfree.dip.cps.cache.message.front.ContainsKeyOfCachePointingStream;

// Created: 07/24/2018, Bing Li
public class ContainsKeyOfCachePointingThreadCreator implements RequestThreadCreatable<ContainsKeyOfCachePointingRequest, ContainsKeyOfCachePointingStream, ContainsKeyOfCachePointingResponse, ContainsKeyOfCachePointingThread>
{

	@Override
	public ContainsKeyOfCachePointingThread createRequestThreadInstance(int taskSize)
	{
		return new ContainsKeyOfCachePointingThread(taskSize);
	}

}
