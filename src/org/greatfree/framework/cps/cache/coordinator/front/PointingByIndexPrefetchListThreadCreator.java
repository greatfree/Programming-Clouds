package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.PointingByIndexPrefetchListRequest;
import org.greatfree.framework.cps.cache.message.front.PointingByIndexPrefetchListResponse;
import org.greatfree.framework.cps.cache.message.front.PointingByIndexPrefetchListStream;

// Created: 08/03/2018, Bing Li
public class PointingByIndexPrefetchListThreadCreator implements RequestQueueCreator<PointingByIndexPrefetchListRequest, PointingByIndexPrefetchListStream, PointingByIndexPrefetchListResponse, PointingByIndexPrefetchListThread>
{

	@Override
	public PointingByIndexPrefetchListThread createInstance(int taskSize)
	{
		return new PointingByIndexPrefetchListThread(taskSize);
	}

}
