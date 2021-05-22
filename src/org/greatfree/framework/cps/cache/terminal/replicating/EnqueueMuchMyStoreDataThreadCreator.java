package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.replicate.EnqueueMuchMyStoreDataNotification;

// Created: 08/13/2018, Bing Li
public class EnqueueMuchMyStoreDataThreadCreator implements NotificationQueueCreator<EnqueueMuchMyStoreDataNotification, EnqueueMuchMyStoreDataThread>
{

	@Override
	public EnqueueMuchMyStoreDataThread createInstance(int taskSize)
	{
		return new EnqueueMuchMyStoreDataThread(taskSize);
	}

}
