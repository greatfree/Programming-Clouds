package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.cps.cache.message.front.LoadMinMyPointingRequest;
import org.greatfree.dsf.cps.cache.message.front.LoadMinMyPointingResponse;
import org.greatfree.dsf.cps.cache.message.front.LoadMinMyPointingStream;

// Created: 07/20/2018, Bing Li
public class LoadMinMyPointingThreadCreator implements RequestThreadCreatable<LoadMinMyPointingRequest, LoadMinMyPointingStream, LoadMinMyPointingResponse, LoadMinMyPointingThread>
{

	@Override
	public LoadMinMyPointingThread createRequestThreadInstance(int taskSize)
	{
		return new LoadMinMyPointingThread(taskSize);
	}

}
