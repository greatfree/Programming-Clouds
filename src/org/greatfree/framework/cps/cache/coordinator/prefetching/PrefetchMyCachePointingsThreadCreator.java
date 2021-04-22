package org.greatfree.framework.cps.cache.coordinator.prefetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.message.FetchMyCachePointingNotification;

// Created: 07/22/2018, Bing Li
public class PrefetchMyCachePointingsThreadCreator implements NotificationObjectThreadCreatable<FetchMyCachePointingNotification, PrefetchMyCachePointingsThread>
{

	@Override
	public PrefetchMyCachePointingsThread createNotificationThreadInstance(int taskSize)
	{
		return new PrefetchMyCachePointingsThread(taskSize);
	}

}
