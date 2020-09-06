package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.cps.cache.message.front.PeekSingleMyStoreDataQueueRequest;
import org.greatfree.dsf.cps.cache.message.front.PeekSingleMyStoreDataQueueResponse;
import org.greatfree.dsf.cps.cache.message.front.PeekSingleMyStoreDataQueueStream;

// Created: 08/14/2018, Bing Li
public class PeekSingleMyStoreDataQueueThreadCreator implements RequestThreadCreatable<PeekSingleMyStoreDataQueueRequest, PeekSingleMyStoreDataQueueStream, PeekSingleMyStoreDataQueueResponse, PeekSingleMyStoreDataQueueThread>
{

	@Override
	public PeekSingleMyStoreDataQueueThread createRequestThreadInstance(int taskSize)
	{
		return new PeekSingleMyStoreDataQueueThread(taskSize);
	}

}
