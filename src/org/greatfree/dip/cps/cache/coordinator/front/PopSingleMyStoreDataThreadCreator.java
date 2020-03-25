package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.front.PopSingleMyStoreDataRequest;
import org.greatfree.dip.cps.cache.message.front.PopSingleMyStoreDataResponse;
import org.greatfree.dip.cps.cache.message.front.PopSingleMyStoreDataStream;

// Created: 08/09/2018, Bing Li
public class PopSingleMyStoreDataThreadCreator implements RequestThreadCreatable<PopSingleMyStoreDataRequest, PopSingleMyStoreDataStream, PopSingleMyStoreDataResponse, PopSingleMyStoreDataThread>
{

	@Override
	public PopSingleMyStoreDataThread createRequestThreadInstance(int taskSize)
	{
		return new PopSingleMyStoreDataThread(taskSize);
	}

}
