package org.greatfree.cluster.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.container.ClusterNotification;

// Created: 09/23/2018, Bing Li
class ChildNotificationThreadCreator implements NotificationQueueCreator<ClusterNotification, ChildNotificationThread>
{

	@Override
	public ChildNotificationThread createInstance(int taskSize)
	{
		return new ChildNotificationThread(taskSize);
	}

}
