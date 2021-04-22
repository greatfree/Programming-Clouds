package org.greatfree.framework.cps.cache.coordinator.evicting;

import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.data.MyPointing;

// Created: 07/11/2018, Bing Li
public class EvictMyPointingThreadCreator implements NotificationObjectThreadCreatable<EvictedNotification<MyPointing>, EvictMyPointingThread>
{

	@Override
	public EvictMyPointingThread createNotificationThreadInstance(int taskSize)
	{
		return new EvictMyPointingThread(taskSize);
	}

}
