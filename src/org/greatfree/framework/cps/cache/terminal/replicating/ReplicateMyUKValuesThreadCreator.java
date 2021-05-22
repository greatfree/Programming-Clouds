package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMyUKValueNotification;

// Created: 02/27/2019, Bing Li
public class ReplicateMyUKValuesThreadCreator implements NotificationQueueCreator<ReplicateMyUKValueNotification, ReplicateMyUKValuesThread>
{

	@Override
	public ReplicateMyUKValuesThread createInstance(int taskSize)
	{
		return new ReplicateMyUKValuesThread(taskSize);
	}

}
