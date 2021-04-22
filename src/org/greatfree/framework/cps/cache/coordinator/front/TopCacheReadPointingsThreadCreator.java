package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.front.TopReadCachePointingsRequest;
import org.greatfree.framework.cps.cache.message.front.TopReadCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.front.TopReadCachePointingsStream;

// Created: 08/05/2018, Bing Li
public class TopCacheReadPointingsThreadCreator implements RequestThreadCreatable<TopReadCachePointingsRequest, TopReadCachePointingsStream, TopReadCachePointingsResponse, TopCacheReadPointingsThread>
{

	@Override
	public TopCacheReadPointingsThread createRequestThreadInstance(int taskSize)
	{
		return new TopCacheReadPointingsThread(taskSize);
	}

}
