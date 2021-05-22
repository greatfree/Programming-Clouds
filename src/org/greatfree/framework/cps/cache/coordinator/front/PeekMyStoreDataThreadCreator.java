package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.PeekMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.front.PeekMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.front.PeekMyStoreDataStream;

// Created: 08/09/2018, Bing Li
public class PeekMyStoreDataThreadCreator implements RequestQueueCreator<PeekMyStoreDataRequest, PeekMyStoreDataStream, PeekMyStoreDataResponse, PeekMyStoreDataThread>
{

	@Override
	public PeekMyStoreDataThread createInstance(int taskSize)
	{
		return new PeekMyStoreDataThread(taskSize);
	}

}
