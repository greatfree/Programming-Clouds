package org.greatfree.cluster.child.container;

import org.greatfree.cluster.message.SelectedChildNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 09/06/2020, Bing Li
class SelectedChildNotificationThreadCreator implements NotificationThreadCreatable<SelectedChildNotification, SelectedChildNotificationThread>
{

	@Override
	public SelectedChildNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new SelectedChildNotificationThread(taskSize);
	}

}
