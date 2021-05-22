package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.DequeueSingleMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.front.DequeueSingleMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.front.DequeueSingleMyStoreDataStream;

// Created: 08/14/2018, Bing Li
public class DequeueSingleMyStoreDataThreadCreator implements RequestQueueCreator<DequeueSingleMyStoreDataRequest, DequeueSingleMyStoreDataStream, DequeueSingleMyStoreDataResponse, DequeueSingleMyStoreDataThread>
{

	@Override
	public DequeueSingleMyStoreDataThread createInstance(int taskSize)
	{
		return new DequeueSingleMyStoreDataThread(taskSize);
	}

}
