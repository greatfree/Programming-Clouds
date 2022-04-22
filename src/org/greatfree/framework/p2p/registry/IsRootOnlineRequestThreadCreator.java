package org.greatfree.framework.p2p.registry;

import org.greatfree.cluster.message.IsRootOnlineRequest;
import org.greatfree.cluster.message.IsRootOnlineResponse;
import org.greatfree.cluster.message.IsRootOnlineStream;
import org.greatfree.concurrency.reactive.RequestQueueCreator;

// Created: 10/27/2018, Bing Li
class IsRootOnlineRequestThreadCreator implements RequestQueueCreator<IsRootOnlineRequest, IsRootOnlineStream, IsRootOnlineResponse, IsRootOnlineRequestThread>
{

	@Override
	public IsRootOnlineRequestThread createInstance(int taskSize)
	{
		return new IsRootOnlineRequestThread(taskSize);
	}

}
