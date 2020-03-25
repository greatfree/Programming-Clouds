package org.greatfree.dip.cps.cache.terminal.prefetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.prefetch.PrefetchMyUKValuesRequest;
import org.greatfree.dip.cps.cache.message.prefetch.PrefetchMyUKValuesResponse;
import org.greatfree.dip.cps.cache.message.prefetch.PrefetchMyUKValuesStream;

// Created: 02/27/2019, Bing Li
public class PrefetchMyUKValuesThreadCreator implements RequestThreadCreatable<PrefetchMyUKValuesRequest, PrefetchMyUKValuesStream, PrefetchMyUKValuesResponse, PrefetchMyUKValuesThread>
{

	@Override
	public PrefetchMyUKValuesThread createRequestThreadInstance(int taskSize)
	{
		return new PrefetchMyUKValuesThread(taskSize);
	}

}
