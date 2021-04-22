package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.front.CachePointingByIndexRequest;
import org.greatfree.framework.cps.cache.message.front.CachePointingByIndexResponse;
import org.greatfree.framework.cps.cache.message.front.CachePointingByIndexStream;

// Created: 07/24/2018, Bing Li
public class CachePointingByIndexThreadCreator implements RequestThreadCreatable<CachePointingByIndexRequest, CachePointingByIndexStream, CachePointingByIndexResponse, CachePointingByIndexThread>
{

	@Override
	public CachePointingByIndexThread createRequestThreadInstance(int taskSize)
	{
		return new CachePointingByIndexThread(taskSize);
	}

}
