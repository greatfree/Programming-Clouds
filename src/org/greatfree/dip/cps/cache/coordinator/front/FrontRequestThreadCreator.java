package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.threetier.message.FrontRequest;
import org.greatfree.dip.cps.threetier.message.FrontResponse;
import org.greatfree.dip.cps.threetier.message.FrontStream;

// Created: 07/07/2018, Bing Li
public class FrontRequestThreadCreator implements RequestThreadCreatable<FrontRequest, FrontStream, FrontResponse, FrontRequestThread>
{

	@Override
	public FrontRequestThread createRequestThreadInstance(int taskSize)
	{
		return new FrontRequestThread(taskSize);
	}

}
