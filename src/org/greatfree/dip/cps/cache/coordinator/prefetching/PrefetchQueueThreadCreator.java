package org.greatfree.dip.cps.cache.coordinator.prefetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dip.cps.cache.message.FetchQueueNotification;

// Created: 08/13/2018, Bing Li
public class PrefetchQueueThreadCreator implements NotificationObjectThreadCreatable<FetchQueueNotification, PrefetchQueueThread>
{

	@Override
	public PrefetchQueueThread createNotificationThreadInstance(int taskSize)
	{
		return new PrefetchQueueThread(taskSize);
	}

}
