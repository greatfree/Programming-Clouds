package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.front.LoadRangeMyUKsRequest;
import org.greatfree.dip.cps.cache.message.front.LoadRangeMyUKsResponse;
import org.greatfree.dip.cps.cache.message.front.LoadRangeMyUKsStream;

// Created: 03/01/2019, Bing Li
public class LoadMyRangeUKsThreadCreator implements RequestThreadCreatable<LoadRangeMyUKsRequest, LoadRangeMyUKsStream, LoadRangeMyUKsResponse, LoadMyRangeUKsThread>
{

	@Override
	public LoadMyRangeUKsThread createRequestThreadInstance(int taskSize)
	{
		return new LoadMyRangeUKsThread(taskSize);
	}

}
