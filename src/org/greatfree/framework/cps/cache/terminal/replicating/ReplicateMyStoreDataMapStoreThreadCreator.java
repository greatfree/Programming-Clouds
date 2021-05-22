package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMyStoreDataMapStoreNotification;

// Created: 08/25/2018, Bing Li
public class ReplicateMyStoreDataMapStoreThreadCreator implements NotificationQueueCreator<ReplicateMyStoreDataMapStoreNotification, ReplicateMyStoreDataMapStoreThread>
{

	@Override
	public ReplicateMyStoreDataMapStoreThread createInstance(int taskSize)
	{
		return new ReplicateMyStoreDataMapStoreThread(taskSize);
	}

}
