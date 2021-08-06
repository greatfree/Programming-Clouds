package org.greatfree.cluster.child.container;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 01/13/2019, Bing Li
class ChildRequestThreadCreator implements NotificationQueueCreator<ClusterRequest, ChildRequestThread>
{

	@Override
	public ChildRequestThread createInstance(int taskSize)
	{
		return new ChildRequestThread(taskSize);
	}

}
