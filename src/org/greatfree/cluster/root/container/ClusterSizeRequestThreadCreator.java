package org.greatfree.cluster.root.container;

import org.greatfree.cluster.message.ClusterSizeRequest;
import org.greatfree.cluster.message.ClusterSizeResponse;
import org.greatfree.cluster.message.ClusterSizeStream;
import org.greatfree.concurrency.reactive.RequestQueueCreator;

// Created: 09/12/2020, Bing Li
class ClusterSizeRequestThreadCreator implements RequestQueueCreator<ClusterSizeRequest, ClusterSizeStream, ClusterSizeResponse, ClusterSizeRequestThread>
{

	@Override
	public ClusterSizeRequestThread createInstance(int taskSize)
	{
		return new ClusterSizeRequestThread(taskSize);
	}

}
