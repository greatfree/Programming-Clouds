package org.greatfree.framework.cps.cache.terminal.prefetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataStream;

// Created: 08/09/2018, Bing Li
public class PopMyStoreDataThreadCreator implements RequestQueueCreator<PopMyStoreDataRequest, PopMyStoreDataStream, PopMyStoreDataResponse, PopMyStoreDataThread>
{

	@Override
	public PopMyStoreDataThread createInstance(int taskSize)
	{
		return new PopMyStoreDataThread(taskSize);
	}

}
