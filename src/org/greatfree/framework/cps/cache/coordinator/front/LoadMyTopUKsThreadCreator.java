package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.LoadTopMyUKsRequest;
import org.greatfree.framework.cps.cache.message.front.LoadTopMyUKsResponse;
import org.greatfree.framework.cps.cache.message.front.LoadTopMyUKsStream;

// Created: 03/01/2019, Bing Li
public class LoadMyTopUKsThreadCreator implements RequestQueueCreator<LoadTopMyUKsRequest, LoadTopMyUKsStream, LoadTopMyUKsResponse, LoadMyTopUKsThread>
{

	@Override
	public LoadMyTopUKsThread createInstance(int taskSize)
	{
		return new LoadMyTopUKsThread(taskSize);
	}

}
