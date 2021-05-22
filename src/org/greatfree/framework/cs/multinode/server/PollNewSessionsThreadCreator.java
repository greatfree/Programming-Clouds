package org.greatfree.framework.cs.multinode.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cs.multinode.message.PollNewSessionsRequest;
import org.greatfree.framework.cs.multinode.message.PollNewSessionsResponse;
import org.greatfree.framework.cs.multinode.message.PollNewSessionsStream;

// Created: 04/24/2017, Bing Li
class PollNewSessionsThreadCreator implements RequestQueueCreator<PollNewSessionsRequest, PollNewSessionsStream, PollNewSessionsResponse, PollNewSessionsThread>
{

	@Override
	public PollNewSessionsThread createInstance(int taskSize)
	{
		return new PollNewSessionsThread(taskSize);
	}

}
