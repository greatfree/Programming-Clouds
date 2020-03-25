package org.greatfree.dip.cps.cache.coordinator.replicating;

import org.greatfree.cache.distributed.MapReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dip.cps.cache.data.MyStoreData;

// Created: 08/24/2018, Bing Li
public class ReplicateMyDistributedStoreDataThreadCreator implements NotificationObjectThreadCreatable<MapReplicateNotification<MyStoreData>, ReplicateMyDistributedStoreDataThread>
{

	@Override
	public ReplicateMyDistributedStoreDataThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateMyDistributedStoreDataThread(taskSize);
	}

}
