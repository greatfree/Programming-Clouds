package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.replicate.PushMuchMyStoreDataNotification;

// Created: 08/09/2018, Bing Li
public class PushMuchMyStoreDataThreadCreator implements NotificationQueueCreator<PushMuchMyStoreDataNotification, PushMuchMyStoreDataThread>
{

	@Override
	public PushMuchMyStoreDataThread createInstance(int taskSize)
	{
		return new PushMuchMyStoreDataThread(taskSize);
	}

}
