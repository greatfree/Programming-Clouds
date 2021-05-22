package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.PeekSingleMyStoreDataQueueRequest;
import org.greatfree.framework.cps.cache.message.front.PeekSingleMyStoreDataQueueResponse;
import org.greatfree.framework.cps.cache.message.front.PeekSingleMyStoreDataQueueStream;

// Created: 08/14/2018, Bing Li
public class PeekSingleMyStoreDataQueueThreadCreator implements RequestQueueCreator<PeekSingleMyStoreDataQueueRequest, PeekSingleMyStoreDataQueueStream, PeekSingleMyStoreDataQueueResponse, PeekSingleMyStoreDataQueueThread>
{

	@Override
	public PeekSingleMyStoreDataQueueThread createInstance(int taskSize)
	{
		return new PeekSingleMyStoreDataQueueThread(taskSize);
	}

}
