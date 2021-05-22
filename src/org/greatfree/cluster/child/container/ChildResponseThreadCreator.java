package org.greatfree.cluster.child.container;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.container.ChildResponse;

// Created: 03/04/2019, Bing Li
class ChildResponseThreadCreator implements NotificationQueueCreator<ChildResponse, ChildResponseThread>
{

	@Override
	public ChildResponseThread createInstance(int taskSize)
	{
		return new ChildResponseThread(taskSize);
	}

}

