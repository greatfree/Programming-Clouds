package org.greatfree.framework.cps.cache.terminal.prefetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyCachePointingsRequest;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyCachePointingsStream;

// Created: 07/25/2018, Bing Li
public class PrefetchMyCachePointingsThreadCreator implements RequestQueueCreator<PrefetchMyCachePointingsRequest, PrefetchMyCachePointingsStream, PrefetchMyCachePointingsResponse, PrefetchMyCachePointingsThread>
{

	@Override
	public PrefetchMyCachePointingsThread createInstance(int taskSize)
	{
		return new PrefetchMyCachePointingsThread(taskSize);
	}

}
