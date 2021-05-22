package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateCachePointingNotification;

// Created: 07/25/2018, Bing Li
public class ReplicateCachePointingThreadCreator implements NotificationQueueCreator<ReplicateCachePointingNotification, ReplicateCachePointingThread>
{

	@Override
	public ReplicateCachePointingThread createInstance(int taskSize)
	{
		return new ReplicateCachePointingThread(taskSize);
	}

}
