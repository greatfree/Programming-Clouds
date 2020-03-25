package org.greatfree.cluster.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.message.multicast.container.ChildResponse;

// Created: 10/02/2018, Bing Li
class ChildResponseThreadCreator implements NotificationThreadCreatable<ChildResponse, ChildResponseThread>
{

	@Override
	public ChildResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new ChildResponseThread(taskSize);
	}

}
