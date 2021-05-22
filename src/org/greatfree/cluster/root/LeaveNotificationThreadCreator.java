package org.greatfree.cluster.root;

import org.greatfree.cluster.message.LeaveNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 10/01/2018, Bing Li
class LeaveNotificationThreadCreator implements NotificationQueueCreator<LeaveNotification, LeaveNotificationThread>
{

	@Override
	public LeaveNotificationThread createInstance(int taskSize)
	{
		return new LeaveNotificationThread(taskSize);
	}

}
