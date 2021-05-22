package org.greatfree.app.p2p;

import org.greatfree.app.p2p.message.GreetingRequest;
import org.greatfree.app.p2p.message.GreetingResponse;
import org.greatfree.app.p2p.message.GreetingStream;
import org.greatfree.concurrency.reactive.RequestQueueCreator;

// Created: 08/19/2018, Bing Li
class GreetingRequestThreadCreator implements RequestQueueCreator<GreetingRequest, GreetingStream, GreetingResponse, GreetingRequestThread>
{

	@Override
	public GreetingRequestThread createInstance(int taskSize)
	{
		return new GreetingRequestThread(taskSize);
	}

}
