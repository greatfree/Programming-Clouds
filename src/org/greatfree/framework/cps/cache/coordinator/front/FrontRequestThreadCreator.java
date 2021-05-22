package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.threetier.message.FrontRequest;
import org.greatfree.framework.cps.threetier.message.FrontResponse;
import org.greatfree.framework.cps.threetier.message.FrontStream;

// Created: 07/07/2018, Bing Li
public class FrontRequestThreadCreator implements RequestQueueCreator<FrontRequest, FrontStream, FrontResponse, FrontRequestThread>
{

	@Override
	public FrontRequestThread createInstance(int taskSize)
	{
		return new FrontRequestThread(taskSize);
	}

}
