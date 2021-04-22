package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 07/07/2018, Bing Li
public class ShutdownCordinatorThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownCordinatorThread>
{

	@Override
	public ShutdownCordinatorThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownCordinatorThread(taskSize);
	}

}
