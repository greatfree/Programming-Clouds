package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.cps.cache.message.front.CachePointingByIndexRequest;
import org.greatfree.dsf.cps.cache.message.front.CachePointingByIndexResponse;
import org.greatfree.dsf.cps.cache.message.front.CachePointingByIndexStream;

// Created: 07/24/2018, Bing Li
public class CachePointingByIndexThreadCreator implements RequestThreadCreatable<CachePointingByIndexRequest, CachePointingByIndexStream, CachePointingByIndexResponse, CachePointingByIndexThread>
{

	@Override
	public CachePointingByIndexThread createRequestThreadInstance(int taskSize)
	{
		return new CachePointingByIndexThread(taskSize);
	}

}
