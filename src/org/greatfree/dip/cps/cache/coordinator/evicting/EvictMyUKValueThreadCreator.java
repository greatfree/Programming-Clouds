package org.greatfree.dip.cps.cache.coordinator.evicting;

import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 02/25/2019, Bing Li
public class EvictMyUKValueThreadCreator implements NotificationObjectThreadCreatable<EvictedNotification<MyUKValue>, EvictMyUKValueThread>
{

	@Override
	public EvictMyUKValueThread createNotificationThreadInstance(int taskSize)
	{
		return new EvictMyUKValueThread(taskSize);
	}

}
