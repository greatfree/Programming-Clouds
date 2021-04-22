package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.cache.message.front.LoadMyPointingRequest;
import org.greatfree.framework.cps.cache.message.front.LoadMyPointingResponse;
import org.greatfree.framework.cps.cache.message.front.LoadMyPointingStream;

// Created: 07/13/2018, Bing Li
public class LoadMyPointingThreadCreator implements RequestThreadCreatable<LoadMyPointingRequest, LoadMyPointingStream, LoadMyPointingResponse, LoadMyPointingThread>
{

	@Override
	public LoadMyPointingThread createRequestThreadInstance(int taskSize)
	{
		return new LoadMyPointingThread(taskSize);
	}

}
