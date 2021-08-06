package org.greatfree.cluster.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 09/23/2018, Bing Li
class ChildRequestThreadCreator implements NotificationQueueCreator<ClusterRequest, ChildRequestThread>
{

	@Override
	public ChildRequestThread createInstance(int taskSize)
	{
		return new ChildRequestThread(taskSize);
	}

}
