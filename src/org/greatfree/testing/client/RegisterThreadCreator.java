package org.greatfree.testing.client;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.testing.message.NodeKeyNotification;

/*
 * The code here attempts to create instances of RegisterThread. It is used by the notification dispatcher. 11/09/2014, Bing Li
 */

// Created: 11/09/2014, Bing Li
public class RegisterThreadCreator implements NotificationThreadCreatable<NodeKeyNotification, RegisterThread>
{
	// Create the instance of RegisterThread. 11/09/2014, Bing Li
	@Override
	public RegisterThread createNotificationThreadInstance(int taskSize)
	{
		return new RegisterThread(taskSize);
	}
}
