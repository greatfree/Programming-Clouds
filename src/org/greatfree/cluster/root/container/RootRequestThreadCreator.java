package org.greatfree.cluster.root.container;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.ClusterRequestStream;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

// Created: 01/13/2019, Bing Li
class RootRequestThreadCreator implements RequestQueueCreator<ClusterRequest, ClusterRequestStream, CollectedClusterResponse, RootRequestThread>
{

	@Override
	public RootRequestThread createInstance(int taskSize)
	{
		return new RootRequestThread(taskSize);
	}

}
