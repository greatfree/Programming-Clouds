package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.front.TopPointingsPrefetchListRequest;
import org.greatfree.dip.cps.cache.message.front.TopPointingsPrefetchListResponse;
import org.greatfree.dip.cps.cache.message.front.TopPointingsPrefetchListStream;

// Created: 08/03/2018, Bing Li
public class TopPointingsPrefetchListThreadCreator implements RequestThreadCreatable<TopPointingsPrefetchListRequest, TopPointingsPrefetchListStream, TopPointingsPrefetchListResponse, TopPointingsPrefetchListThread>
{

	@Override
	public TopPointingsPrefetchListThread createRequestThreadInstance(int taskSize)
	{
		return new TopPointingsPrefetchListThread(taskSize);
	}

}
