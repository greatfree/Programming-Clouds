package org.greatfree.cluster.root;

import org.greatfree.cluster.message.JoinNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 10/01/2018, Bing Li
class JoinNotificationThreadCreator implements NotificationThreadCreatable<JoinNotification, JoinNotificationThread>
{

	@Override
	public JoinNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new JoinNotificationThread(taskSize);
	}

}
