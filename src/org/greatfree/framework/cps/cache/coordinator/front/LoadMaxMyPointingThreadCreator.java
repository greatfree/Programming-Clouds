package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.LoadMaxMyPointingRequest;
import org.greatfree.framework.cps.cache.message.front.LoadMaxMyPointingResponse;
import org.greatfree.framework.cps.cache.message.front.LoadMaxMyPointingStream;

// Created: 07/20/2018, Bing Li
public class LoadMaxMyPointingThreadCreator implements RequestQueueCreator<LoadMaxMyPointingRequest, LoadMaxMyPointingStream, LoadMaxMyPointingResponse, LoadMaxMyPointingThread>
{

	@Override
	public LoadMaxMyPointingThread createInstance(int taskSize)
	{
		return new LoadMaxMyPointingThread(taskSize);
	}

}
