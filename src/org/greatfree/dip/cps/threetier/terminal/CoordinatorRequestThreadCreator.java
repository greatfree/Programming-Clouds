package org.greatfree.dip.cps.threetier.terminal;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cps.threetier.message.CoordinatorRequest;
import org.greatfree.dip.cps.threetier.message.CoordinatorResponse;
import org.greatfree.dip.cps.threetier.message.CoordinatorStream;

// Created: 07/07/2018, Bing Li
class CoordinatorRequestThreadCreator implements RequestThreadCreatable<CoordinatorRequest, CoordinatorStream, CoordinatorResponse, CoordinatorRequestThread>
{

	@Override
	public CoordinatorRequestThread createRequestThreadInstance(int taskSize)
	{
		return new CoordinatorRequestThread(taskSize);
	}

}
