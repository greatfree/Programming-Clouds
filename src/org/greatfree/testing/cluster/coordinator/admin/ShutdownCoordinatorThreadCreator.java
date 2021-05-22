package org.greatfree.testing.cluster.coordinator.admin;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.ShutdownCoordinatorNotification;

/*
 * The creator here attempts to create instances of ShutdownCoordinatorThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class ShutdownCoordinatorThreadCreator implements NotificationQueueCreator<ShutdownCoordinatorNotification, ShutdownCoordinatorThread>
{

	@Override
	public ShutdownCoordinatorThread createInstance(int taskSize)
	{
		return new ShutdownCoordinatorThread(taskSize);
	}

}
