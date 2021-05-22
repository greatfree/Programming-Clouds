package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMuchMyDataNotification;

// Created: 07/30/2018, Bing Li
public class ReplicateMuchMyDataThreadCreator implements NotificationQueueCreator<ReplicateMuchMyDataNotification, ReplicateMuchMyDataThread>
{

	@Override
	public ReplicateMuchMyDataThread createInstance(int taskSize)
	{
		return new ReplicateMuchMyDataThread(taskSize);
	}

}
