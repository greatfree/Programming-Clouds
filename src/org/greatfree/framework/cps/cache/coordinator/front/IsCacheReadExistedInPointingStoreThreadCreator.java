package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.IsCacheReadExistedInPointingStoreRequest;
import org.greatfree.framework.cps.cache.message.front.IsCacheReadExistedInPointingStoreResponse;
import org.greatfree.framework.cps.cache.message.front.IsCacheReadExistedInPointingStoreStream;

// Created: 08/05/2018, Bing Li
public class IsCacheReadExistedInPointingStoreThreadCreator implements RequestQueueCreator<IsCacheReadExistedInPointingStoreRequest, IsCacheReadExistedInPointingStoreStream, IsCacheReadExistedInPointingStoreResponse, IsCacheReadExistedInPointingStoreThread>
{

	@Override
	public IsCacheReadExistedInPointingStoreThread createInstance(int taskSize)
	{
		return new IsCacheReadExistedInPointingStoreThread(taskSize);
	}

}
