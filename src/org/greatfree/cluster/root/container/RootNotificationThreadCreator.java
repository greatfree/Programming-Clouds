package org.greatfree.cluster.root.container;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.container.Notification;

// Created: 01/13/2019, Bing Li
class RootNotificationThreadCreator implements NotificationQueueCreator<Notification, RootNotificationThread>
{

	@Override
	public RootNotificationThread createInstance(int taskSize)
	{
		return new RootNotificationThread(taskSize);
	}

}
