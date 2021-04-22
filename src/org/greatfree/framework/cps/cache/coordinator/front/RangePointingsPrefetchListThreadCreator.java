package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.front.RangePointingsPrefetchListRequest;
import org.greatfree.framework.cps.cache.message.front.RangePointingsPrefetchListResponse;
import org.greatfree.framework.cps.cache.message.front.RangePointingsPrefetchListStream;

// Created: 08/03/2018, Bing Li
public class RangePointingsPrefetchListThreadCreator implements RequestThreadCreatable<RangePointingsPrefetchListRequest, RangePointingsPrefetchListStream, RangePointingsPrefetchListResponse, RangePointingsPrefetchListThread>
{

	@Override
	public RangePointingsPrefetchListThread createRequestThreadInstance(int taskSize)
	{
		return new RangePointingsPrefetchListThread(taskSize);
	}

}
