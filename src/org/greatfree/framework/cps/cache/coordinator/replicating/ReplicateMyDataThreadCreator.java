package org.greatfree.framework.cps.cache.coordinator.replicating;

import org.greatfree.cache.distributed.MapReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.data.MyData;

// Created: 07/09/2018, Bing Li
public class ReplicateMyDataThreadCreator implements NotificationObjectThreadCreatable<MapReplicateNotification<MyData>, ReplicateMyDataThread>
{

	@Override
	public ReplicateMyDataThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateMyDataThread(taskSize);
	}

}
