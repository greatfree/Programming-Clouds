package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMyDataNotification;

// Created: 07/09/2018, Bing Li
public class ReplicateMyDataThreadCreator implements NotificationQueueCreator<ReplicateMyDataNotification, ReplicateMyDataThread>
{

	@Override
	public ReplicateMyDataThread createInstance(int taskSize)
	{
		return new ReplicateMyDataThread(taskSize);
	}

}
