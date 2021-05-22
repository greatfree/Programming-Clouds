package org.greatfree.cluster.child.container;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.container.Notification;

// Created: 01/13/2019, Bing Li
class ChildNotificationThreadCreator implements NotificationQueueCreator<Notification, ChildNotificationThread>
{

	@Override
	public ChildNotificationThread createInstance(int taskSize)
	{
		return new ChildNotificationThread(taskSize);
	}

}
