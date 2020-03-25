package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.front.LoadMyPointingRequest;
import org.greatfree.dip.cps.cache.message.front.LoadMyPointingResponse;
import org.greatfree.dip.cps.cache.message.front.LoadMyPointingStream;

// Created: 07/13/2018, Bing Li
public class LoadMyPointingThreadCreator implements RequestThreadCreatable<LoadMyPointingRequest, LoadMyPointingStream, LoadMyPointingResponse, LoadMyPointingThread>
{

	@Override
	public LoadMyPointingThread createRequestThreadInstance(int taskSize)
	{
		return new LoadMyPointingThread(taskSize);
	}

}
