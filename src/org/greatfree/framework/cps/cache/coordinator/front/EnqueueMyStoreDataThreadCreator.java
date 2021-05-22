package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.replicate.EnqueueMyStoreDataNotification;

// Created: 08/13/2018, Bing Li
public class EnqueueMyStoreDataThreadCreator implements NotificationQueueCreator<EnqueueMyStoreDataNotification, EnqueueMyStoreDataThread>
{

	@Override
	public EnqueueMyStoreDataThread createInstance(int taskSize)
	{
		return new EnqueueMyStoreDataThread(taskSize);
	}

}
