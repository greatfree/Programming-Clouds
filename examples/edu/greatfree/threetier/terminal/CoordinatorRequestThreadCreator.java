package edu.greatfree.threetier.terminal;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;

import edu.greatfree.threetier.message.CoordinatorRequest;
import edu.greatfree.threetier.message.CoordinatorResponse;
import edu.greatfree.threetier.message.CoordinatorStream;

// Created: 07/07/2018, Bing Li
class CoordinatorRequestThreadCreator implements RequestThreadCreatable<CoordinatorRequest, CoordinatorStream, CoordinatorResponse, CoordinatorRequestThread>
{

	@Override
	public CoordinatorRequestThread createRequestThreadInstance(int taskSize)
	{
		return new CoordinatorRequestThread(taskSize);
	}

}
