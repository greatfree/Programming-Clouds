package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.front.DequeueSingleMyStoreDataRequest;
import org.greatfree.dip.cps.cache.message.front.DequeueSingleMyStoreDataResponse;
import org.greatfree.dip.cps.cache.message.front.DequeueSingleMyStoreDataStream;

// Created: 08/14/2018, Bing Li
public class DequeueSingleMyStoreDataThreadCreator implements RequestThreadCreatable<DequeueSingleMyStoreDataRequest, DequeueSingleMyStoreDataStream, DequeueSingleMyStoreDataResponse, DequeueSingleMyStoreDataThread>
{

	@Override
	public DequeueSingleMyStoreDataThread createRequestThreadInstance(int taskSize)
	{
		return new DequeueSingleMyStoreDataThread(taskSize);
	}

}
