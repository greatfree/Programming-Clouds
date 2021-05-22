package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.TopPointingsPrefetchListRequest;
import org.greatfree.framework.cps.cache.message.front.TopPointingsPrefetchListResponse;
import org.greatfree.framework.cps.cache.message.front.TopPointingsPrefetchListStream;

// Created: 08/03/2018, Bing Li
public class TopPointingsPrefetchListThreadCreator implements RequestQueueCreator<TopPointingsPrefetchListRequest, TopPointingsPrefetchListStream, TopPointingsPrefetchListResponse, TopPointingsPrefetchListThread>
{

	@Override
	public TopPointingsPrefetchListThread createInstance(int taskSize)
	{
		return new TopPointingsPrefetchListThread(taskSize);
	}

}
