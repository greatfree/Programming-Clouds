package org.greatfree.dip.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dip.cps.cache.message.replicate.ReplicateMyDataNotification;

// Created: 07/09/2018, Bing Li
public class ReplicateMyDataThreadCreator implements NotificationThreadCreatable<ReplicateMyDataNotification, ReplicateMyDataThread>
{

	@Override
	public ReplicateMyDataThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateMyDataThread(taskSize);
	}

}
