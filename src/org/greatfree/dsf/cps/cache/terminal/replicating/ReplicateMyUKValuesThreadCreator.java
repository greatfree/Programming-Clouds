package org.greatfree.dsf.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.cps.cache.message.replicate.ReplicateMyUKValueNotification;

// Created: 02/27/2019, Bing Li
public class ReplicateMyUKValuesThreadCreator implements NotificationThreadCreatable<ReplicateMyUKValueNotification, ReplicateMyUKValuesThread>
{

	@Override
	public ReplicateMyUKValuesThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateMyUKValuesThread(taskSize);
	}

}
