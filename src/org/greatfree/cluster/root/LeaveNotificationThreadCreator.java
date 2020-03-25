package org.greatfree.cluster.root;

import org.greatfree.cluster.message.LeaveNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 10/01/2018, Bing Li
class LeaveNotificationThreadCreator implements NotificationThreadCreatable<LeaveNotification, LeaveNotificationThread>
{

	@Override
	public LeaveNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new LeaveNotificationThread(taskSize);
	}

}
