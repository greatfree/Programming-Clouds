package org.greatfree.framework.cps.threetier.terminal;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.threetier.message.CoordinatorNotification;

// Created: 
class CoordinatorNotificationThreadCreator implements NotificationQueueCreator<CoordinatorNotification, CoordinatorNotificationThread>
{

	@Override
	public CoordinatorNotificationThread createInstance(int taskSize)
	{
		return new CoordinatorNotificationThread(taskSize);
	}

}
