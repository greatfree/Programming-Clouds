package org.greatfree.cluster.root.container;

import org.greatfree.cluster.message.LeaveNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 01/13/2019, Bing Li
class LeaveNotificationThreadCreator implements NotificationQueueCreator<LeaveNotification, LeaveNotificationThread>
{

	@Override
	public LeaveNotificationThread createInstance(int taskSize)
	{
		return new LeaveNotificationThread(taskSize);
	}

}
