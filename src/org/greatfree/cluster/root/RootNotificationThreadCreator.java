package org.greatfree.cluster.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.message.multicast.container.Notification;

// Created: 09/23/2018, Bing Li
class RootNotificationThreadCreator implements NotificationThreadCreatable<Notification, RootNotificationThread>
{

	@Override
	public RootNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new RootNotificationThread(taskSize);
	}

}
