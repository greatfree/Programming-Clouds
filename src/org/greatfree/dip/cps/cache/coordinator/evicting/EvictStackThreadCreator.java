package org.greatfree.dip.cps.cache.coordinator.evicting;

import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dip.cps.cache.data.MyStoreData;

// Created: 08/08/2018, Bing Li
public class EvictStackThreadCreator implements NotificationObjectThreadCreatable<EvictedNotification<MyStoreData>, EvictStackThread>
{

	@Override
	public EvictStackThread createNotificationThreadInstance(int taskSize)
	{
		return new EvictStackThread(taskSize);
	}

}
