package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.PopSingleMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.front.PopSingleMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.front.PopSingleMyStoreDataStream;

// Created: 08/09/2018, Bing Li
public class PopSingleMyStoreDataThreadCreator implements RequestQueueCreator<PopSingleMyStoreDataRequest, PopSingleMyStoreDataStream, PopSingleMyStoreDataResponse, PopSingleMyStoreDataThread>
{

	@Override
	public PopSingleMyStoreDataThread createInstance(int taskSize)
	{
		return new PopSingleMyStoreDataThread(taskSize);
	}

}
