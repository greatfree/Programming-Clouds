package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.threetier.message.FrontNotification;

// Created: 07/07/2018, Bing Li
public class FrontNotificationThreadCreator implements NotificationQueueCreator<FrontNotification, FrontNotificationThread>
{

	@Override
	public FrontNotificationThread createInstance(int taskSize)
	{
		return new FrontNotificationThread(taskSize);
	}

}
