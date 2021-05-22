package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.IsCacheEmptyInPointingStoreRequest;
import org.greatfree.framework.cps.cache.message.front.IsCacheEmptyInPointingStoreResponse;
import org.greatfree.framework.cps.cache.message.front.IsCacheEmptyInPointingStoreStream;

// Created: 07/24/2018, Bing Li
public class IsCacheEmptyInPointingStoreThreadCreator implements RequestQueueCreator<IsCacheEmptyInPointingStoreRequest, IsCacheEmptyInPointingStoreStream, IsCacheEmptyInPointingStoreResponse, IsCacheEmptyInPointingStoreThread>
{

	@Override
	public IsCacheEmptyInPointingStoreThread createInstance(int taskSize)
	{
		return new IsCacheEmptyInPointingStoreThread(taskSize);
	}

}
