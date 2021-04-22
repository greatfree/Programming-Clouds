package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMyPointingNotification;

// Created: 07/12/2018, Bing Li
public class ReplicateMyPointingThreadCreator implements NotificationThreadCreatable<ReplicateMyPointingNotification, ReplicateMyPointingThread>
{

	@Override
	public ReplicateMyPointingThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateMyPointingThread(taskSize);
	}

}
