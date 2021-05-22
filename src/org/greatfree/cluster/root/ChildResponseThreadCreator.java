package org.greatfree.cluster.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.container.ChildResponse;

// Created: 10/02/2018, Bing Li
class ChildResponseThreadCreator implements NotificationQueueCreator<ChildResponse, ChildResponseThread>
{

	@Override
	public ChildResponseThread createInstance(int taskSize)
	{
		return new ChildResponseThread(taskSize);
	}

}
