package org.greatfree.app.cps.coordinator;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 08/14/2018, Bing Li
public class ShutdownCordinatorThreadCreator implements NotificationQueueCreator<ShutdownServerNotification, ShutdownCordinatorThread>
{

	@Override
	public ShutdownCordinatorThread createInstance(int taskSize)
	{
		return new ShutdownCordinatorThread(taskSize);
	}

}
