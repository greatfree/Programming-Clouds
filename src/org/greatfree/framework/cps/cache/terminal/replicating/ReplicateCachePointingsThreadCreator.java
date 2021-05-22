package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateCachePointingsNotification;

// Created: 07/25/2018, Bing Li
public class ReplicateCachePointingsThreadCreator implements NotificationQueueCreator<ReplicateCachePointingsNotification, ReplicateCachePointingsThread>
{

	@Override
	public ReplicateCachePointingsThread createInstance(int taskSize)
	{
		return new ReplicateCachePointingsThread(taskSize);
	}

}
