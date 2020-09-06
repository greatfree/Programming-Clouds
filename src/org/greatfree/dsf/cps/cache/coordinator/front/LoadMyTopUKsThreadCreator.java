package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.cps.cache.message.front.LoadTopMyUKsRequest;
import org.greatfree.dsf.cps.cache.message.front.LoadTopMyUKsResponse;
import org.greatfree.dsf.cps.cache.message.front.LoadTopMyUKsStream;

// Created: 03/01/2019, Bing Li
public class LoadMyTopUKsThreadCreator implements RequestThreadCreatable<LoadTopMyUKsRequest, LoadTopMyUKsStream, LoadTopMyUKsResponse, LoadMyTopUKsThread>
{

	@Override
	public LoadMyTopUKsThread createRequestThreadInstance(int taskSize)
	{
		return new LoadMyTopUKsThread(taskSize);
	}

}
