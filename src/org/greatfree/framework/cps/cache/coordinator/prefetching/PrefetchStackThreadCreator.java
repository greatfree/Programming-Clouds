package org.greatfree.framework.cps.cache.coordinator.prefetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.message.FetchStackNotification;

// Created: 08/07/2018, Bing Li
public class PrefetchStackThreadCreator implements NotificationObjectThreadCreatable<FetchStackNotification, PrefetchStackThread>
{

	@Override
	public PrefetchStackThread createNotificationThreadInstance(int taskSize)
	{
		return new PrefetchStackThread(taskSize);
	}

}
