package org.greatfree.dsf.cps.cache.coordinator.replicating;

import org.greatfree.cache.distributed.PointingReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dsf.cps.cache.data.MyPointing;

// Created: 07/11/2018, Bing Li
public class ReplicateMyPointingThreadCreator implements NotificationObjectThreadCreatable<PointingReplicateNotification<MyPointing>, ReplicateMyPointingThread>
{

	@Override
	public ReplicateMyPointingThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateMyPointingThread(taskSize);
	}

}
