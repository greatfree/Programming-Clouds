package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.front.RangeReadCachePointingsRequest;
import org.greatfree.dip.cps.cache.message.front.RangeReadCachePointingsResponse;
import org.greatfree.dip.cps.cache.message.front.RangeReadCachePointingsStream;

// Created: 08/05/2018, Bing Li
public class LoadRangeReadMyPointingsThreadCreator implements RequestThreadCreatable<RangeReadCachePointingsRequest, RangeReadCachePointingsStream, RangeReadCachePointingsResponse, LoadRangeReadMyPointingsThread>
{

	@Override
	public LoadRangeReadMyPointingsThread createRequestThreadInstance(int taskSize)
	{
		return new LoadRangeReadMyPointingsThread(taskSize);
	}

}
