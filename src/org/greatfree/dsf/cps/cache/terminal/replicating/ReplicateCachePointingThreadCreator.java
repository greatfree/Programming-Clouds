package org.greatfree.dsf.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.cps.cache.message.replicate.ReplicateCachePointingNotification;

// Created: 07/25/2018, Bing Li
public class ReplicateCachePointingThreadCreator implements NotificationThreadCreatable<ReplicateCachePointingNotification, ReplicateCachePointingThread>
{

	@Override
	public ReplicateCachePointingThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateCachePointingThread(taskSize);
	}

}
