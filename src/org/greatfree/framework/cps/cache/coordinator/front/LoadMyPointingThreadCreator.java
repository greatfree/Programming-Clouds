package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.cache.message.front.LoadMyPointingRequest;
import org.greatfree.framework.cps.cache.message.front.LoadMyPointingResponse;
import org.greatfree.framework.cps.cache.message.front.LoadMyPointingStream;

// Created: 07/13/2018, Bing Li
public class LoadMyPointingThreadCreator implements RequestQueueCreator<LoadMyPointingRequest, LoadMyPointingStream, LoadMyPointingResponse, LoadMyPointingThread>
{

	@Override
	public LoadMyPointingThread createInstance(int taskSize)
	{
		return new LoadMyPointingThread(taskSize);
	}

}
