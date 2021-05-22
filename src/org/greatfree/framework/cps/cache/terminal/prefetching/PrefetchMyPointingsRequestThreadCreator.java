package org.greatfree.framework.cps.cache.terminal.prefetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyPointingsRequest;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyPointingsResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyPointingsStream;

// Created: 07/12/2018, Bing Li
public class PrefetchMyPointingsRequestThreadCreator implements RequestQueueCreator<PrefetchMyPointingsRequest, PrefetchMyPointingsStream, PrefetchMyPointingsResponse, PrefetchMyPointingsRequestThread>
{

	@Override
	public PrefetchMyPointingsRequestThread createInstance(int taskSize)
	{
		return new PrefetchMyPointingsRequestThread(taskSize);
	}

}
