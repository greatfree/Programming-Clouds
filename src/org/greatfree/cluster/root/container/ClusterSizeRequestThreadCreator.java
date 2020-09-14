package org.greatfree.cluster.root.container;

import org.greatfree.cluster.message.ClusterSizeRequest;
import org.greatfree.cluster.message.ClusterSizeResponse;
import org.greatfree.cluster.message.ClusterSizeStream;
import org.greatfree.concurrency.reactive.RequestThreadCreatable;

// Created: 09/12/2020, Bing Li
class ClusterSizeRequestThreadCreator implements RequestThreadCreatable<ClusterSizeRequest, ClusterSizeStream, ClusterSizeResponse, ClusterSizeRequestThread>
{

	@Override
	public ClusterSizeRequestThread createRequestThreadInstance(int taskSize)
	{
		return new ClusterSizeRequestThread(taskSize);
	}

}
