package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.front.IsCacheReadExistedInPointingStoreRequest;
import org.greatfree.dip.cps.cache.message.front.IsCacheReadExistedInPointingStoreResponse;
import org.greatfree.dip.cps.cache.message.front.IsCacheReadExistedInPointingStoreStream;

// Created: 08/05/2018, Bing Li
public class IsCacheReadExistedInPointingStoreThreadCreator implements RequestThreadCreatable<IsCacheReadExistedInPointingStoreRequest, IsCacheReadExistedInPointingStoreStream, IsCacheReadExistedInPointingStoreResponse, IsCacheReadExistedInPointingStoreThread>
{

	@Override
	public IsCacheReadExistedInPointingStoreThread createRequestThreadInstance(int taskSize)
	{
		return new IsCacheReadExistedInPointingStoreThread(taskSize);
	}

}
