package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMuchMyStoreDataMapStoreNotification;

// Created: 08/25/2018, Bing Li
public class ReplicateMuchMyStoreDataMapStoreThreadCreator implements NotificationThreadCreatable<ReplicateMuchMyStoreDataMapStoreNotification, ReplicateMuchMyStoreDataMapStoreThread>
{

	@Override
	public ReplicateMuchMyStoreDataMapStoreThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateMuchMyStoreDataMapStoreThread(taskSize);
	}

}
