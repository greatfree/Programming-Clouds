package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.front.IsCacheEmptyInPointingStoreRequest;
import org.greatfree.framework.cps.cache.message.front.IsCacheEmptyInPointingStoreResponse;
import org.greatfree.framework.cps.cache.message.front.IsCacheEmptyInPointingStoreStream;

// Created: 07/24/2018, Bing Li
public class IsCacheEmptyInPointingStoreThreadCreator implements RequestThreadCreatable<IsCacheEmptyInPointingStoreRequest, IsCacheEmptyInPointingStoreStream, IsCacheEmptyInPointingStoreResponse, IsCacheEmptyInPointingStoreThread>
{

	@Override
	public IsCacheEmptyInPointingStoreThread createRequestThreadInstance(int taskSize)
	{
		return new IsCacheEmptyInPointingStoreThread(taskSize);
	}

}
