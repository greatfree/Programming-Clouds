package org.greatfree.framework.cps.cache.terminal.prefetching;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataStream;

// Created: 08/09/2018, Bing Li
public class PopMyStoreDataThreadCreator implements RequestThreadCreatable<PopMyStoreDataRequest, PopMyStoreDataStream, PopMyStoreDataResponse, PopMyStoreDataThread>
{

	@Override
	public PopMyStoreDataThread createRequestThreadInstance(int taskSize)
	{
		return new PopMyStoreDataThread(taskSize);
	}

}
