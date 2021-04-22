package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMyStoreDataMapStoreNotification;

// Created: 08/25/2018, Bing Li
public class ReplicateMyStoreDataMapStoreThreadCreator implements NotificationThreadCreatable<ReplicateMyStoreDataMapStoreNotification, ReplicateMyStoreDataMapStoreThread>
{

	@Override
	public ReplicateMyStoreDataMapStoreThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateMyStoreDataMapStoreThread(taskSize);
	}

}
