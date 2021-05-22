package org.greatfree.framework.cps.cache.terminal.prefetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyUKValuesRequest;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyUKValuesResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyUKValuesStream;

// Created: 02/27/2019, Bing Li
public class PrefetchMyUKValuesThreadCreator implements RequestQueueCreator<PrefetchMyUKValuesRequest, PrefetchMyUKValuesStream, PrefetchMyUKValuesResponse, PrefetchMyUKValuesThread>
{

	@Override
	public PrefetchMyUKValuesThread createInstance(int taskSize)
	{
		return new PrefetchMyUKValuesThread(taskSize);
	}

}
