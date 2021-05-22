package org.greatfree.cluster.child.container;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 01/26/2019, Bing Li
class IntercastNotificationThreadCreator implements NotificationQueueCreator<IntercastNotification, IntercastNotificationThread>
{

	@Override
	public IntercastNotificationThread createInstance(int taskSize)
	{
		return new IntercastNotificationThread(taskSize);
	}

}


