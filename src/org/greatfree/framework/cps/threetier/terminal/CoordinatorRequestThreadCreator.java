package org.greatfree.framework.cps.threetier.terminal;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cps.threetier.message.CoordinatorRequest;
import org.greatfree.framework.cps.threetier.message.CoordinatorResponse;
import org.greatfree.framework.cps.threetier.message.CoordinatorStream;

// Created: 07/07/2018, Bing Li
class CoordinatorRequestThreadCreator implements RequestQueueCreator<CoordinatorRequest, CoordinatorStream, CoordinatorResponse, CoordinatorRequestThread>
{

	@Override
	public CoordinatorRequestThread createInstance(int taskSize)
	{
		return new CoordinatorRequestThread(taskSize);
	}

}
