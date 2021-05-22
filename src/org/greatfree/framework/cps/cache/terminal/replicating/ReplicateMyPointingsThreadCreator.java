package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMyPointingsNotification;

// Created: 07/12/2018, Bing Li
public class ReplicateMyPointingsThreadCreator implements NotificationQueueCreator<ReplicateMyPointingsNotification, ReplicateMyPointingsThread>
{

	@Override
	public ReplicateMyPointingsThread createInstance(int taskSize)
	{
		return new ReplicateMyPointingsThread(taskSize);
	}

}
