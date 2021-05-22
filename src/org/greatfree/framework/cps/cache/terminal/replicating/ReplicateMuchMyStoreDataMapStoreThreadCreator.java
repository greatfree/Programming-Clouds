package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMuchMyStoreDataMapStoreNotification;

// Created: 08/25/2018, Bing Li
public class ReplicateMuchMyStoreDataMapStoreThreadCreator implements NotificationQueueCreator<ReplicateMuchMyStoreDataMapStoreNotification, ReplicateMuchMyStoreDataMapStoreThread>
{

	@Override
	public ReplicateMuchMyStoreDataMapStoreThread createInstance(int taskSize)
	{
		return new ReplicateMuchMyStoreDataMapStoreThread(taskSize);
	}

}
