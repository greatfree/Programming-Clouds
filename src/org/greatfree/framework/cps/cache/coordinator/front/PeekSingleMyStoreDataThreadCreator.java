package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.front.PeekSingleMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.front.PeekSingleMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.front.PeekSingleMyStoreDataStream;

// Created: 08/09/2018, Bing Li
public class PeekSingleMyStoreDataThreadCreator implements RequestThreadCreatable<PeekSingleMyStoreDataRequest, PeekSingleMyStoreDataStream, PeekSingleMyStoreDataResponse, PeekSingleMyStoreDataThread>
{

	@Override
	public PeekSingleMyStoreDataThread createRequestThreadInstance(int taskSize)
	{
		return new PeekSingleMyStoreDataThread(taskSize);
	}

}
