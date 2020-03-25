package org.greatfree.cluster.root.container;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.message.multicast.container.Notification;

// Created: 01/13/2019, Bing Li
class RootNotificationThreadCreator implements NotificationThreadCreatable<Notification, RootNotificationThread>
{

	@Override
	public RootNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new RootNotificationThread(taskSize);
	}

}
