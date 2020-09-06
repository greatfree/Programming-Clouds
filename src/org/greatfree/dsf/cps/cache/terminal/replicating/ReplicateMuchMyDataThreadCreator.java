package org.greatfree.dsf.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.cps.cache.message.replicate.ReplicateMuchMyDataNotification;

// Created: 07/30/2018, Bing Li
public class ReplicateMuchMyDataThreadCreator implements NotificationThreadCreatable<ReplicateMuchMyDataNotification, ReplicateMuchMyDataThread>
{

	@Override
	public ReplicateMuchMyDataThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateMuchMyDataThread(taskSize);
	}

}
