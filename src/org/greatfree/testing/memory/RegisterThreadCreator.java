package org.greatfree.testing.memory;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.testing.message.NodeKeyNotification;

/*
 * The code here attempts to create instances of RegisterThread. It is used by the notification dispatcher. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class RegisterThreadCreator implements NotificationThreadCreatable<NodeKeyNotification, RegisterThread>
{
	@Override
	public RegisterThread createNotificationThreadInstance(int taskSize)
	{
		return new RegisterThread(taskSize);
	}
}
