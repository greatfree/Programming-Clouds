package org.greatfree.dip.cps.cache.coordinator.evicting;

import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dip.cps.cache.data.MyCacheTiming;

// Created: 08/19/2018, Bing Li
public class EvictMyCacheTimingThreadCreator implements NotificationObjectThreadCreatable<EvictedNotification<MyCacheTiming>, EvictMyCacheTimingThread>
{

	@Override
	public EvictMyCacheTimingThread createNotificationThreadInstance(int taskSize)
	{
		return new EvictMyCacheTimingThread(taskSize);
	}

}
