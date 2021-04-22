package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.cps.cache.message.replicate.PushMuchMyStoreDataNotification;

// Created: 08/09/2018, Bing Li
public class PushMuchMyStoreDataThreadCreator implements NotificationThreadCreatable<PushMuchMyStoreDataNotification, PushMuchMyStoreDataThread>
{

	@Override
	public PushMuchMyStoreDataThread createNotificationThreadInstance(int taskSize)
	{
		return new PushMuchMyStoreDataThread(taskSize);
	}

}
