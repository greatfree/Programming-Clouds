package org.greatfree.testing.coordinator.memorizing;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.testing.message.UnregisterMemoryServerNotification;

/*
 * The creator here attempts to create instances of UnregisterMemoryServerThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class UnregisterMemoryServerThreadCreator implements NotificationThreadCreatable<UnregisterMemoryServerNotification, UnregisterMemoryServerThread>
{
	@Override
	public UnregisterMemoryServerThread createNotificationThreadInstance(int taskSize)
	{
		return new UnregisterMemoryServerThread(taskSize);
	}
}
