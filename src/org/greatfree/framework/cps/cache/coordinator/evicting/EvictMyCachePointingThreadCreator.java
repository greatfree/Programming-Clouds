package org.greatfree.framework.cps.cache.coordinator.evicting;

import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.data.MyCachePointing;

// Created: 07/22/2018, Bing Li
public class EvictMyCachePointingThreadCreator implements NotificationObjectThreadCreatable<EvictedNotification<MyCachePointing>, EvictMyCachePointingThread>
{

	@Override
	public EvictMyCachePointingThread createNotificationThreadInstance(int taskSize)
	{
		return new EvictMyCachePointingThread(taskSize);
	}

}
