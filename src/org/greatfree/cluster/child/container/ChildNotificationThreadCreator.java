package org.greatfree.cluster.child.container;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.message.multicast.container.Notification;

// Created: 01/13/2019, Bing Li
class ChildNotificationThreadCreator implements NotificationThreadCreatable<Notification, ChildNotificationThread>
{

	@Override
	public ChildNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ChildNotificationThread(taskSize);
	}

}
