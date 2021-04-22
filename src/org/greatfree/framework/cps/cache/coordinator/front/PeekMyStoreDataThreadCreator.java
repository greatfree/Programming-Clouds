package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.front.PeekMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.front.PeekMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.front.PeekMyStoreDataStream;

// Created: 08/09/2018, Bing Li
public class PeekMyStoreDataThreadCreator implements RequestThreadCreatable<PeekMyStoreDataRequest, PeekMyStoreDataStream, PeekMyStoreDataResponse, PeekMyStoreDataThread>
{

	@Override
	public PeekMyStoreDataThread createRequestThreadInstance(int taskSize)
	{
		return new PeekMyStoreDataThread(taskSize);
	}

}
