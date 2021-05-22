package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.LoadMinMyPointingRequest;
import org.greatfree.framework.cps.cache.message.front.LoadMinMyPointingResponse;
import org.greatfree.framework.cps.cache.message.front.LoadMinMyPointingStream;

// Created: 07/20/2018, Bing Li
public class LoadMinMyPointingThreadCreator implements RequestQueueCreator<LoadMinMyPointingRequest, LoadMinMyPointingStream, LoadMinMyPointingResponse, LoadMinMyPointingThread>
{

	@Override
	public LoadMinMyPointingThread createInstance(int taskSize)
	{
		return new LoadMinMyPointingThread(taskSize);
	}

}
