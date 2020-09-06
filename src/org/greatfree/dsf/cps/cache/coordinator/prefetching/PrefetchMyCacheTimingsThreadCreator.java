package org.greatfree.dsf.cps.cache.coordinator.prefetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dsf.cps.cache.message.FetchMyCacheTimingNotification;

// Created: 08/19/2018, Bing Li
public class PrefetchMyCacheTimingsThreadCreator implements NotificationObjectThreadCreatable<FetchMyCacheTimingNotification, PrefetchMyCacheTimingsThread>
{

	@Override
	public PrefetchMyCacheTimingsThread createNotificationThreadInstance(int taskSize)
	{
		return new PrefetchMyCacheTimingsThread(taskSize);
	}

}
