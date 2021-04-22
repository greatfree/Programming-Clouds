package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.front.IsCacheExistedInPointingStoreRequest;
import org.greatfree.framework.cps.cache.message.front.IsCacheExistedInPointingStoreResponse;
import org.greatfree.framework.cps.cache.message.front.IsCacheExistedInPointingStoreStream;

// Created: 07/24/2018, Bing Li
public class IsCacheExistedInPointingStoreThreadCreator implements RequestThreadCreatable<IsCacheExistedInPointingStoreRequest, IsCacheExistedInPointingStoreStream, IsCacheExistedInPointingStoreResponse, IsCacheExistedInPointingStoreThread>
{

	@Override
	public IsCacheExistedInPointingStoreThread createRequestThreadInstance(int taskSize)
	{
		return new IsCacheExistedInPointingStoreThread(taskSize);
	}

}
