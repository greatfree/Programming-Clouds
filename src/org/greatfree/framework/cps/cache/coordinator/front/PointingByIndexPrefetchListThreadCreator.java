package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.front.PointingByIndexPrefetchListRequest;
import org.greatfree.framework.cps.cache.message.front.PointingByIndexPrefetchListResponse;
import org.greatfree.framework.cps.cache.message.front.PointingByIndexPrefetchListStream;

// Created: 08/03/2018, Bing Li
public class PointingByIndexPrefetchListThreadCreator implements RequestThreadCreatable<PointingByIndexPrefetchListRequest, PointingByIndexPrefetchListStream, PointingByIndexPrefetchListResponse, PointingByIndexPrefetchListThread>
{

	@Override
	public PointingByIndexPrefetchListThread createRequestThreadInstance(int taskSize)
	{
		return new PointingByIndexPrefetchListThread(taskSize);
	}

}
