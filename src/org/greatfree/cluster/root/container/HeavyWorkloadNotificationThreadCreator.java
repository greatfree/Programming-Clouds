package org.greatfree.cluster.root.container;

import org.greatfree.cluster.message.HeavyWorkloadNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 09/06/2020, Bing Li
class HeavyWorkloadNotificationThreadCreator implements NotificationQueueCreator<HeavyWorkloadNotification, HeavyWorkloadNotificationThread>
{

	@Override
	public HeavyWorkloadNotificationThread createInstance(int taskSize)
	{
		return new HeavyWorkloadNotificationThread(taskSize);
	}

}
