package org.greatfree.framework.cps.cache.coordinator.replicating;

import org.greatfree.cache.distributed.store.ReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.data.MyStoreData;

// Created: 08/07/2018, Bing Li
public class ReplicateStackThreadCreator implements NotificationObjectThreadCreatable<ReplicateNotification<MyStoreData>, ReplicateStackThread>
{

	@Override
	public ReplicateStackThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateStackThread(taskSize);
	}

}
