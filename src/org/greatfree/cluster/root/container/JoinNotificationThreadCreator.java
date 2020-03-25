package org.greatfree.cluster.root.container;

import org.greatfree.cluster.message.JoinNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 01/13/2019, Bing Li
class JoinNotificationThreadCreator implements NotificationThreadCreatable<JoinNotification, JoinNotificationThread>
{

	@Override
	public JoinNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new JoinNotificationThread(taskSize);
	}

}
