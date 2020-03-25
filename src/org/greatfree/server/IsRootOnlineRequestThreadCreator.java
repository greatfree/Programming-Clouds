package org.greatfree.server;

import org.greatfree.cluster.message.IsRootOnlineRequest;
import org.greatfree.cluster.message.IsRootOnlineResponse;
import org.greatfree.cluster.message.IsRootOnlineStream;
import org.greatfree.concurrency.reactive.RequestThreadCreatable;

// Created: 10/27/2018, Bing Li
class IsRootOnlineRequestThreadCreator implements RequestThreadCreatable<IsRootOnlineRequest, IsRootOnlineStream, IsRootOnlineResponse, IsRootOnlineRequestThread>
{

	@Override
	public IsRootOnlineRequestThread createRequestThreadInstance(int taskSize)
	{
		return new IsRootOnlineRequestThread(taskSize);
	}

}
