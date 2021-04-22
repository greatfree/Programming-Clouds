package org.greatfree.framework.cps.threetier.terminal;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.cps.threetier.message.CoordinatorNotification;

// Created: 
class CoordinatorNotificationThreadCreator implements NotificationThreadCreatable<CoordinatorNotification, CoordinatorNotificationThread>
{

	@Override
	public CoordinatorNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new CoordinatorNotificationThread(taskSize);
	}

}
