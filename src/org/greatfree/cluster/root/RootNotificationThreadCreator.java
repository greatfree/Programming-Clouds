package org.greatfree.cluster.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.container.ClusterNotification;

// Created: 09/23/2018, Bing Li
class RootNotificationThreadCreator implements NotificationQueueCreator<ClusterNotification, RootNotificationThread>
{

	@Override
	public RootNotificationThread createInstance(int taskSize)
	{
		return new RootNotificationThread(taskSize);
	}

}
