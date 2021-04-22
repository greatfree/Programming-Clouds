package org.greatfree.framework.cps.cache.coordinator.evicting;

import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.data.MyData;

// Created: 07/09/2018, Bing Li
public class EvictMyDataThreadCreator implements NotificationObjectThreadCreatable<EvictedNotification<MyData>, EvictMyDataThread>
{

	@Override
	public EvictMyDataThread createNotificationThreadInstance(int taskSize)
	{
		return new EvictMyDataThread(taskSize);
	}

}
