package org.greatfree.cluster.root.container;

import org.greatfree.cluster.message.HeavyWorkloadNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 09/06/2020, Bing Li
class HeavyWorkloadNotificationThreadCreator implements NotificationThreadCreatable<HeavyWorkloadNotification, HeavyWorkloadNotificationThread>
{

	@Override
	public HeavyWorkloadNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new HeavyWorkloadNotificationThread(taskSize);
	}

}
