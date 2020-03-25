package org.greatfree.cluster.child.container;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.message.multicast.container.ChildResponse;

// Created: 03/04/2019, Bing Li
class ChildResponseThreadCreator implements NotificationThreadCreatable<ChildResponse, ChildResponseThread>
{

	@Override
	public ChildResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new ChildResponseThread(taskSize);
	}

}

