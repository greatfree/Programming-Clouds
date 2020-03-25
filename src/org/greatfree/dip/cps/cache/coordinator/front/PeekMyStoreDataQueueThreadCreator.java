package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.front.PeekMyStoreDataQueueRequest;
import org.greatfree.dip.cps.cache.message.front.PeekMyStoreDataQueueResponse;
import org.greatfree.dip.cps.cache.message.front.PeekMyStoreDataQueueStream;

// Created: 08/14/2018, Bing Li
public class PeekMyStoreDataQueueThreadCreator implements RequestThreadCreatable<PeekMyStoreDataQueueRequest, PeekMyStoreDataQueueStream, PeekMyStoreDataQueueResponse, PeekMyStoreDataQueueThread>
{

	@Override
	public PeekMyStoreDataQueueThread createRequestThreadInstance(int taskSize)
	{
		return new PeekMyStoreDataQueueThread(taskSize);
	}

}
