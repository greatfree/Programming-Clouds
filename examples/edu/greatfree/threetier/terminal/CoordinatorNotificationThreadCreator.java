package edu.greatfree.threetier.terminal;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import edu.greatfree.threetier.message.CoordinatorNotification;

// Created: 
class CoordinatorNotificationThreadCreator implements NotificationThreadCreatable<CoordinatorNotification, CoordinatorNotificationThread>
{

	@Override
	public CoordinatorNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new CoordinatorNotificationThread(taskSize);
	}

}
