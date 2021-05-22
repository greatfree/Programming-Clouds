package org.greatfree.framework.cps.cache.terminal.prefetching;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.prefetch.DequeueMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.prefetch.DequeueMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.prefetch.DequeueMyStoreDataStream;

// Created: 08/13/2018, Bing Li
public class DequeueMyStoreDataThreadCreator implements RequestQueueCreator<DequeueMyStoreDataRequest, DequeueMyStoreDataStream, DequeueMyStoreDataResponse, DequeueMyStoreDataThread>
{

	@Override
	public DequeueMyStoreDataThread createInstance(int taskSize)
	{
		return new DequeueMyStoreDataThread(taskSize);
	}

}
