package org.greatfree.dsf.cps.cache.coordinator.evicting;

import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dsf.cps.cache.data.MyPointing;

// Created: 07/19/2018, Bing Li
public class EvictMyPointingMapThreadCreator implements NotificationObjectThreadCreatable<EvictedNotification<MyPointing>, EvictMyPointingMapThread>
{

	@Override
	public EvictMyPointingMapThread createNotificationThreadInstance(int taskSize)
	{
		return new EvictMyPointingMapThread(taskSize);
	}

}
