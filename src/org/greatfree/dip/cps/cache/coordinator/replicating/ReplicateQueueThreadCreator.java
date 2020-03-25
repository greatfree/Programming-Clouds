package org.greatfree.dip.cps.cache.coordinator.replicating;

import org.greatfree.cache.distributed.store.ReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dip.cps.cache.data.MyStoreData;

// Created: 08/13/2018, Bing Li
public class ReplicateQueueThreadCreator implements NotificationObjectThreadCreatable<ReplicateNotification<MyStoreData>, ReplicateQueueThread>
{

	@Override
	public ReplicateQueueThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateQueueThread(taskSize);
	}

}
