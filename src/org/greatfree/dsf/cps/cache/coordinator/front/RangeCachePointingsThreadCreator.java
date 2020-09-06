package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.cps.cache.message.front.RangeCachePointingsRequest;
import org.greatfree.dsf.cps.cache.message.front.RangeCachePointingsResponse;
import org.greatfree.dsf.cps.cache.message.front.RangeCachePointingsStream;

// Created: 07/24/2018, Bing Li
public class RangeCachePointingsThreadCreator implements RequestThreadCreatable<RangeCachePointingsRequest, RangeCachePointingsStream, RangeCachePointingsResponse, RangeCachePointingsThread>
{

	@Override
	public RangeCachePointingsThread createRequestThreadInstance(int taskSize)
	{
		return new RangeCachePointingsThread(taskSize);
	}

}
