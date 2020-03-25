package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.cache.message.front.LoadMaxMyPointingRequest;
import org.greatfree.dip.cps.cache.message.front.LoadMaxMyPointingResponse;
import org.greatfree.dip.cps.cache.message.front.LoadMaxMyPointingStream;

// Created: 07/20/2018, Bing Li
public class LoadMaxMyPointingThreadCreator implements RequestThreadCreatable<LoadMaxMyPointingRequest, LoadMaxMyPointingStream, LoadMaxMyPointingResponse, LoadMaxMyPointingThread>
{

	@Override
	public LoadMaxMyPointingThread createRequestThreadInstance(int taskSize)
	{
		return new LoadMaxMyPointingThread(taskSize);
	}

}
