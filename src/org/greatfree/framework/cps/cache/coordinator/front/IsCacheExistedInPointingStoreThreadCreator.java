package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.IsCacheExistedInPointingStoreRequest;
import org.greatfree.framework.cps.cache.message.front.IsCacheExistedInPointingStoreResponse;
import org.greatfree.framework.cps.cache.message.front.IsCacheExistedInPointingStoreStream;

// Created: 07/24/2018, Bing Li
public class IsCacheExistedInPointingStoreThreadCreator implements RequestQueueCreator<IsCacheExistedInPointingStoreRequest, IsCacheExistedInPointingStoreStream, IsCacheExistedInPointingStoreResponse, IsCacheExistedInPointingStoreThread>
{

	@Override
	public IsCacheExistedInPointingStoreThread createInstance(int taskSize)
	{
		return new IsCacheExistedInPointingStoreThread(taskSize);
	}

}
