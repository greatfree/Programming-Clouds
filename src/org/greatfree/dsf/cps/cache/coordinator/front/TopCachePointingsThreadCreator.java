package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.cps.cache.message.front.TopCachePointingsRequest;
import org.greatfree.dsf.cps.cache.message.front.TopCachePointingsResponse;
import org.greatfree.dsf.cps.cache.message.front.TopCachePointingsStream;

// Created: 07/24/2018, Bing Li
public class TopCachePointingsThreadCreator implements RequestThreadCreatable<TopCachePointingsRequest, TopCachePointingsStream, TopCachePointingsResponse, TopCachePointingsThread>
{

	@Override
	public TopCachePointingsThread createRequestThreadInstance(int taskSize)
	{
		return new TopCachePointingsThread(taskSize);
	}

}
