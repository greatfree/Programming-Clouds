package org.greatfree.dsf.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.cps.cache.message.replicate.EnqueueMuchMyStoreDataNotification;

// Created: 08/13/2018, Bing Li
public class EnqueueMuchMyStoreDataThreadCreator implements NotificationThreadCreatable<EnqueueMuchMyStoreDataNotification, EnqueueMuchMyStoreDataThread>
{

	@Override
	public EnqueueMuchMyStoreDataThread createNotificationThreadInstance(int taskSize)
	{
		return new EnqueueMuchMyStoreDataThread(taskSize);
	}

}
