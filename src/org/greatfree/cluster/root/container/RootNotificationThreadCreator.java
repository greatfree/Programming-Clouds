package org.greatfree.cluster.root.container;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.container.ClusterNotification;

// Created: 01/13/2019, Bing Li
class RootNotificationThreadCreator implements NotificationQueueCreator<ClusterNotification, RootNotificationThread>
{

	@Override
	public RootNotificationThread createInstance(int taskSize)
	{
		return new RootNotificationThread(taskSize);
	}

}
