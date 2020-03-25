package org.greatfree.cluster.child.container;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.message.multicast.container.Request;

// Created: 01/13/2019, Bing Li
class ChildRequestThreadCreator implements NotificationThreadCreatable<Request, ChildRequestThread>
{

	@Override
	public ChildRequestThread createNotificationThreadInstance(int taskSize)
	{
		return new ChildRequestThread(taskSize);
	}

}
