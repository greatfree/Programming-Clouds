package org.greatfree.testing.coordinator.memorizing;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.RegisterMemoryServerNotification;

/*
 * The creator here attempts to create instances of RegisterMemoryServerThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class RegisterMemoryServerThreadCreator implements NotificationQueueCreator<RegisterMemoryServerNotification, RegisterMemoryServerThread>
{
	@Override
	public RegisterMemoryServerThread createInstance(int taskSize)
	{
		return new RegisterMemoryServerThread(taskSize);
	}
}
