package org.greatfree.cluster.root;

import org.greatfree.cluster.message.JoinNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 10/01/2018, Bing Li
class JoinNotificationThreadCreator implements NotificationQueueCreator<JoinNotification, JoinNotificationThread>
{

	@Override
	public JoinNotificationThread createInstance(int taskSize)
	{
		return new JoinNotificationThread(taskSize);
	}

}
