package org.greatfree.cluster.root.container;

import org.greatfree.cluster.message.LeaveNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 01/13/2019, Bing Li
class LeaveNotificationThreadCreator implements NotificationThreadCreatable<LeaveNotification, LeaveNotificationThread>
{

	@Override
	public LeaveNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new LeaveNotificationThread(taskSize);
	}

}
