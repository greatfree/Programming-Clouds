package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMyPointingsNotification;

// Created: 07/12/2018, Bing Li
public class ReplicateMyPointingsThreadCreator implements NotificationThreadCreatable<ReplicateMyPointingsNotification, ReplicateMyPointingsThread>
{

	@Override
	public ReplicateMyPointingsThread createNotificationThreadInstance(int taskSize)
	{
		return new ReplicateMyPointingsThread(taskSize);
	}

}
