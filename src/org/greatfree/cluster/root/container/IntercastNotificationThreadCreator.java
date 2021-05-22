package org.greatfree.cluster.root.container;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 03/04/2019, Bing Li
class IntercastNotificationThreadCreator implements NotificationQueueCreator<IntercastNotification, IntercastNotificationThread>
{

	@Override
	public IntercastNotificationThread createInstance(int taskSize)
	{
		return new IntercastNotificationThread(taskSize);
	}

}
