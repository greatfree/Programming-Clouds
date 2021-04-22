package org.greatfree.framework.cps.cache.terminal.man;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.cps.threetier.message.CoordinatorRequest;
import org.greatfree.framework.cps.threetier.message.CoordinatorResponse;
import org.greatfree.framework.cps.threetier.message.CoordinatorStream;

// Created: 07/07/2018, Bing Li
public class CoordinatorRequestThreadCreator implements RequestThreadCreatable<CoordinatorRequest, CoordinatorStream, CoordinatorResponse, CoordinatorRequestThread>
{

	@Override
	public CoordinatorRequestThread createRequestThreadInstance(int taskSize)
	{
		return new CoordinatorRequestThread(taskSize);
	}

}
