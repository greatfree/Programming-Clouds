package org.greatfree.cluster.root.container;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 03/04/2019, Bing Li
class IntercastNotificationThreadCreator implements NotificationThreadCreatable<IntercastNotification, IntercastNotificationThread>
{

	@Override
	public IntercastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new IntercastNotificationThread(taskSize);
	}

}
