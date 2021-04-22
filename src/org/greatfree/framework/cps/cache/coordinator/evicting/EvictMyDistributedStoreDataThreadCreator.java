package org.greatfree.framework.cps.cache.coordinator.evicting;

import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.data.MyStoreData;

// Created: 08/24/2018, Bing Li
public class EvictMyDistributedStoreDataThreadCreator implements NotificationObjectThreadCreatable<EvictedNotification<MyStoreData>, EvictMyDistributedStoreDataThread>
{

	@Override
	public EvictMyDistributedStoreDataThread createNotificationThreadInstance(int taskSize)
	{
		return new EvictMyDistributedStoreDataThread(taskSize);
	}

}
