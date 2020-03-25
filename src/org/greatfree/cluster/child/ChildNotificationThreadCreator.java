package org.greatfree.cluster.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.message.multicast.container.Notification;

// Created: 09/23/2018, Bing Li
class ChildNotificationThreadCreator implements NotificationThreadCreatable<Notification, ChildNotificationThread>
{

	@Override
	public ChildNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ChildNotificationThread(taskSize);
	}

}
