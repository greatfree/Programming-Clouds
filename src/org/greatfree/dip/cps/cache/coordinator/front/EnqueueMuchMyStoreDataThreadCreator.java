package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dip.cps.cache.message.replicate.EnqueueMuchMyStoreDataNotification;

// Created: 08/13/2018, Bing Li
public class EnqueueMuchMyStoreDataThreadCreator implements NotificationThreadCreatable<EnqueueMuchMyStoreDataNotification, EnqueueMuchMyStoreDataThread>
{

	@Override
	public EnqueueMuchMyStoreDataThread createNotificationThreadInstance(int taskSize)
	{
		return new EnqueueMuchMyStoreDataThread(taskSize);
	}

}
