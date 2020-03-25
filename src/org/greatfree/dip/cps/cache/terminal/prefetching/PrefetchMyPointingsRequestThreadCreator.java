package org.greatfree.dip.cps.cache.terminal.prefetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.prefetch.PrefetchMyPointingsRequest;
import org.greatfree.dip.cps.cache.message.prefetch.PrefetchMyPointingsResponse;
import org.greatfree.dip.cps.cache.message.prefetch.PrefetchMyPointingsStream;

// Created: 07/12/2018, Bing Li
public class PrefetchMyPointingsRequestThreadCreator implements RequestThreadCreatable<PrefetchMyPointingsRequest, PrefetchMyPointingsStream, PrefetchMyPointingsResponse, PrefetchMyPointingsRequestThread>
{

	@Override
	public PrefetchMyPointingsRequestThread createRequestThreadInstance(int taskSize)
	{
		return new PrefetchMyPointingsRequestThread(taskSize);
	}

}
