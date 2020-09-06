package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.cps.cache.message.front.CachePointingByKeyRequest;
import org.greatfree.dsf.cps.cache.message.front.CachePointingByKeyResponse;
import org.greatfree.dsf.cps.cache.message.front.CachePointingByKeyStream;

// Created: 07/24/2018, Bing Li
public class CachePointingByKeyThreadCreator implements RequestThreadCreatable<CachePointingByKeyRequest, CachePointingByKeyStream, CachePointingByKeyResponse, CachePointingByKeyThread>
{

	@Override
	public CachePointingByKeyThread createRequestThreadInstance(int taskSize)
	{
		return new CachePointingByKeyThread(taskSize);
	}

}
