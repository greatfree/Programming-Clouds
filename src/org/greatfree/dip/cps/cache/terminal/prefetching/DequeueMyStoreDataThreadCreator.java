package org.greatfree.dip.cps.cache.terminal.prefetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.prefetch.DequeueMyStoreDataRequest;
import org.greatfree.dip.cps.cache.message.prefetch.DequeueMyStoreDataResponse;
import org.greatfree.dip.cps.cache.message.prefetch.DequeueMyStoreDataStream;

// Created: 08/13/2018, Bing Li
public class DequeueMyStoreDataThreadCreator implements RequestThreadCreatable<DequeueMyStoreDataRequest, DequeueMyStoreDataStream, DequeueMyStoreDataResponse, DequeueMyStoreDataThread>
{

	@Override
	public DequeueMyStoreDataThread createRequestThreadInstance(int taskSize)
	{
		return new DequeueMyStoreDataThread(taskSize);
	}

}
