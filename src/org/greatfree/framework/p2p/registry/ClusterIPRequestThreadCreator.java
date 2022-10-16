package org.greatfree.framework.p2p.registry;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.message.multicast.ClusterIPRequest;
import org.greatfree.message.multicast.ClusterIPResponse;
import org.greatfree.message.multicast.ClusterIPStream;

/*
 * The creator generates one instance of ClusterIPRequestThread. It is invoked by the thread management mechanism, RequestDispatcher. 04/30/2017, Bing Li
 */

// Created: 05/08/2017, Bing Li
class ClusterIPRequestThreadCreator implements RequestQueueCreator<ClusterIPRequest, ClusterIPStream, ClusterIPResponse, ClusterIPRequestThread>
{

	@Override
	public ClusterIPRequestThread createInstance(int taskSize)
	{
		return new ClusterIPRequestThread(taskSize);
	}

}
