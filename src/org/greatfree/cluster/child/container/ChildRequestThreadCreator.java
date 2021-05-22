package org.greatfree.cluster.child.container;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.container.Request;

// Created: 01/13/2019, Bing Li
class ChildRequestThreadCreator implements NotificationQueueCreator<Request, ChildRequestThread>
{

	@Override
	public ChildRequestThread createInstance(int taskSize)
	{
		return new ChildRequestThread(taskSize);
	}

}
