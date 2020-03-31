package edu.greatfree.threetier.coordinator;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;

import edu.greatfree.threetier.message.FrontRequest;
import edu.greatfree.threetier.message.FrontResponse;
import edu.greatfree.threetier.message.FrontStream;

// Created: 07/07/2018, Bing Li
class FrontRequestThreadCreator implements RequestThreadCreatable<FrontRequest, FrontStream, FrontResponse, FrontRequestThread>
{

	@Override
	public FrontRequestThread createRequestThreadInstance(int taskSize)
	{
		return new FrontRequestThread(taskSize);
	}

}
