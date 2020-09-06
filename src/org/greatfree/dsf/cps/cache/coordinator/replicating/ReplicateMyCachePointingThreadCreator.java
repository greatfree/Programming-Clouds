package org.greatfree.dsf.cps.cache.coordinator.replicating;

import org.greatfree.cache.distributed.PointingReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dsf.cps.cache.data.MyCachePointing;

// Created: 07/22/2018, Bing Li
public class ReplicateMyCachePointingThreadCreator implements NotificationObjectThreadCreatable<PointingReplicateNotification<MyCachePointing>, ReplicateMyCachePointingThread>
{

	@Override
	public ReplicateMyCachePointingThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateMyCachePointingThread(taskSize);
	}

}
