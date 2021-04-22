package org.greatfree.framework.cps.cache.coordinator.prefetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.message.FetchMyCachePointingNotification;

// Created: 08/23/2018, Bing Li
public class PrefetchMyCacheReadPointingsThreadCreator implements NotificationObjectThreadCreatable<FetchMyCachePointingNotification, PrefetchMyCacheReadPointingsThread>
{

	@Override
	public PrefetchMyCacheReadPointingsThread createNotificationThreadInstance(int taskSize)
	{
		return new PrefetchMyCacheReadPointingsThread(taskSize);
	}

}
