package org.greatfree.dsf.cps.threetier.coordinator;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 07/07/2018, Bing Li
class ShutdownCordinatorThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownCordinatorThread>
{

	@Override
	public ShutdownCordinatorThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownCordinatorThread(taskSize);
	}

}
