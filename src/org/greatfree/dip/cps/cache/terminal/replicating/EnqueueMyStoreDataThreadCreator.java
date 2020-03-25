package org.greatfree.dip.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dip.cps.cache.message.replicate.EnqueueMyStoreDataNotification;

// Created: 08/13/2018, Bing Li
public class EnqueueMyStoreDataThreadCreator implements NotificationThreadCreatable<EnqueueMyStoreDataNotification, EnqueueMyStoreDataThread>
{

	@Override
	public EnqueueMyStoreDataThread createNotificationThreadInstance(int taskSize)
	{
		return new EnqueueMyStoreDataThread(taskSize);
	}

}
