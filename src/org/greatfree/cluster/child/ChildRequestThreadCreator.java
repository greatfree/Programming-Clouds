package org.greatfree.cluster.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.message.multicast.container.Request;

// Created: 09/23/2018, Bing Li
class ChildRequestThreadCreator implements NotificationThreadCreatable<Request, ChildRequestThread>
{

	@Override
	public ChildRequestThread createNotificationThreadInstance(int taskSize)
	{
		return new ChildRequestThread(taskSize);
	}

}
