package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.LoadRangeMyUKsRequest;
import org.greatfree.framework.cps.cache.message.front.LoadRangeMyUKsResponse;
import org.greatfree.framework.cps.cache.message.front.LoadRangeMyUKsStream;

// Created: 03/01/2019, Bing Li
public class LoadMyRangeUKsThreadCreator implements RequestQueueCreator<LoadRangeMyUKsRequest, LoadRangeMyUKsStream, LoadRangeMyUKsResponse, LoadMyRangeUKsThread>
{

	@Override
	public LoadMyRangeUKsThread createInstance(int taskSize)
	{
		return new LoadMyRangeUKsThread(taskSize);
	}

}
