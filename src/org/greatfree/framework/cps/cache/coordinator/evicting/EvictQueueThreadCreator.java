package org.greatfree.framework.cps.cache.coordinator.evicting;

import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.data.MyStoreData;

// Created: 08/13/2018, Bing Li
public class EvictQueueThreadCreator implements NotificationObjectThreadCreatable<EvictedNotification<MyStoreData>, EvictQueueThread>
{

	@Override
	public EvictQueueThread createNotificationThreadInstance(int taskSize)
	{
		return new EvictQueueThread(taskSize);
	}

}
