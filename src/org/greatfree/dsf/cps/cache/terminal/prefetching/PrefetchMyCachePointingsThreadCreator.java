package org.greatfree.dsf.cps.cache.terminal.prefetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.cps.cache.message.prefetch.PrefetchMyCachePointingsRequest;
import org.greatfree.dsf.cps.cache.message.prefetch.PrefetchMyCachePointingsResponse;
import org.greatfree.dsf.cps.cache.message.prefetch.PrefetchMyCachePointingsStream;

// Created: 07/25/2018, Bing Li
public class PrefetchMyCachePointingsThreadCreator implements RequestThreadCreatable<PrefetchMyCachePointingsRequest, PrefetchMyCachePointingsStream, PrefetchMyCachePointingsResponse, PrefetchMyCachePointingsThread>
{

	@Override
	public PrefetchMyCachePointingsThread createRequestThreadInstance(int taskSize)
	{
		return new PrefetchMyCachePointingsThread(taskSize);
	}

}
