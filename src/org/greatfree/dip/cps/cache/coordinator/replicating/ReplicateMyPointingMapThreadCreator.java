package org.greatfree.dip.cps.cache.coordinator.replicating;

import org.greatfree.cache.distributed.PointingReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dip.cps.cache.data.MyPointing;

// Created: 07/19/2018, Bing Li
public class ReplicateMyPointingMapThreadCreator implements NotificationObjectThreadCreatable<PointingReplicateNotification<MyPointing>, ReplicateMyPointingMapThread>
{

	@Override
	public ReplicateMyPointingMapThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateMyPointingMapThread(taskSize);
	}

}
