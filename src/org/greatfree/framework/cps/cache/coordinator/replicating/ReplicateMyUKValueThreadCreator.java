package org.greatfree.framework.cps.cache.coordinator.replicating;

import org.greatfree.cache.distributed.ListReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 02/25/2019, Bing Li
public class ReplicateMyUKValueThreadCreator implements NotificationObjectThreadCreatable<ListReplicateNotification<MyUKValue>, ReplicateMyUKValueThread>
{

	@Override
	public ReplicateMyUKValueThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateMyUKValueThread(taskSize);
	}

}
