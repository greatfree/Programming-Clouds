package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.CachePointingByKeyRequest;
import org.greatfree.framework.cps.cache.message.front.CachePointingByKeyResponse;
import org.greatfree.framework.cps.cache.message.front.CachePointingByKeyStream;

// Created: 07/24/2018, Bing Li
public class CachePointingByKeyThreadCreator implements RequestQueueCreator<CachePointingByKeyRequest, CachePointingByKeyStream, CachePointingByKeyResponse, CachePointingByKeyThread>
{

	@Override
	public CachePointingByKeyThread createInstance(int taskSize)
	{
		return new CachePointingByKeyThread(taskSize);
	}

}
