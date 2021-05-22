package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.PeekMyStoreDataQueueRequest;
import org.greatfree.framework.cps.cache.message.front.PeekMyStoreDataQueueResponse;
import org.greatfree.framework.cps.cache.message.front.PeekMyStoreDataQueueStream;

// Created: 08/14/2018, Bing Li
public class PeekMyStoreDataQueueThreadCreator implements RequestQueueCreator<PeekMyStoreDataQueueRequest, PeekMyStoreDataQueueStream, PeekMyStoreDataQueueResponse, PeekMyStoreDataQueueThread>
{

	@Override
	public PeekMyStoreDataQueueThread createInstance(int taskSize)
	{
		return new PeekMyStoreDataQueueThread(taskSize);
	}

}
