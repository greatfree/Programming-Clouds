package org.greatfree.cluster.child.container;

import org.greatfree.cluster.message.SelectedChildNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Since the message, SelectedChildNotification, needs to be forwarded/broadcast, it inherits Notification. The message is processed in the thread,  ChildNotificationThread. So the below lines is NOT useful. 09/12/2020, Bing Li

// Created: 09/06/2020, Bing Li
class SelectedChildNotificationThreadCreator implements NotificationQueueCreator<SelectedChildNotification, SelectedChildNotificationThread>
{

	@Override
	public SelectedChildNotificationThread createInstance(int taskSize)
	{
		return new SelectedChildNotificationThread(taskSize);
	}

}
