package org.greatfree.framework.cps.cache.coordinator.replicating;

import org.greatfree.cache.distributed.PointingReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.data.MyCacheTiming;

// Created: 08/19/2018, Bing Li
//public class ReplicateMyCacheTimingThreadCreator implements NotificationObjectThreadCreatable<TimingReplicateNotification<MyCacheTiming>, ReplicateMyCacheTimingThread>
public class ReplicateMyCacheTimingThreadCreator implements NotificationObjectThreadCreatable<PointingReplicateNotification<MyCacheTiming>, ReplicateMyCacheTimingThread>
{

	@Override
	public ReplicateMyCacheTimingThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateMyCacheTimingThread(taskSize);
	}

}
