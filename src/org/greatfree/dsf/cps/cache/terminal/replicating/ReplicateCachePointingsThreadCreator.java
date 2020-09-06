package org.greatfree.dsf.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.cps.cache.message.replicate.ReplicateCachePointingsNotification;

// Created: 07/25/2018, Bing Li
public class ReplicateCachePointingsThreadCreator implements NotificationThreadCreatable<ReplicateCachePointingsNotification, ReplicateCachePointingsThread>
{

	@Override
	public ReplicateCachePointingsThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateCachePointingsThread(taskSize);
	}

}
