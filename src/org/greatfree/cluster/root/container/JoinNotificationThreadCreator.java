package org.greatfree.cluster.root.container;

import org.greatfree.cluster.message.JoinNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 01/13/2019, Bing Li
class JoinNotificationThreadCreator implements NotificationQueueCreator<JoinNotification, JoinNotificationThread>
{

	@Override
	public JoinNotificationThread createInstance(int taskSize)
	{
		return new JoinNotificationThread(taskSize);
	}

}
