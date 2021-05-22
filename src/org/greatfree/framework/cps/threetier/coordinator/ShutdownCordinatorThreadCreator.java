package org.greatfree.framework.cps.threetier.coordinator;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 07/07/2018, Bing Li
class ShutdownCordinatorThreadCreator implements NotificationQueueCreator<ShutdownServerNotification, ShutdownCordinatorThread>
{

	@Override
	public ShutdownCordinatorThread createInstance(int taskSize)
	{
		return new ShutdownCordinatorThread(taskSize);
	}

}
