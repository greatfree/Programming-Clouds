package org.greatfree.testing.server;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.testing.message.ShutdownServerNotification;

/*
 * The code here attempts to create instances of ShutdownThread. 01/20/2016, Bing Li
 */

// Created: 01/20/2016, Bing Li
public class ShutdownThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownThread>
{
	@Override
	public ShutdownThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownThread(taskSize);
	}
}
