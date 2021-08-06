package org.greatfree.cluster.root;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.ClusterRequestStream;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

// Created: 09/23/2018, Bing Li
class RootRequestThreadCreator implements RequestQueueCreator<ClusterRequest, ClusterRequestStream, CollectedClusterResponse, RootRequestThread>
{

	@Override
	public RootRequestThread createInstance(int taskSize)
	{
		return new RootRequestThread(taskSize);
	}

}
