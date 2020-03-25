package org.greatfree.testing.cluster.coordinator.client;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.testing.message.UnregisterClientNotification;

/*
 * The creator here attempts to create instances of UnregisterClientThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/28/2014, Bing Li
 */

// Created: 11/21/2016, Bing Li
public class UnregisterClientThreadCreator implements NotificationThreadCreatable<UnregisterClientNotification, UnregisterClientThread>
{

	@Override
	public UnregisterClientThread createNotificationThreadInstance(int taskSize)
	{
		return new UnregisterClientThread(taskSize);
	}

}
