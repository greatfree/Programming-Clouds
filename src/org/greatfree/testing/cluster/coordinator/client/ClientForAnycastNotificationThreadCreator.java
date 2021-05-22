package org.greatfree.testing.cluster.coordinator.client;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.ClientForAnycastNotification;

/*
 * The creator here attempts to create instances of ClientForAnycastNotificationThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/27/2014, Bing Li
 */

// Created: 11/21/2016, Bing Li
public class ClientForAnycastNotificationThreadCreator implements NotificationQueueCreator<ClientForAnycastNotification, ClientForAnycastNotificationThread>
{

	@Override
	public ClientForAnycastNotificationThread createInstance(int taskSize)
	{
		return new ClientForAnycastNotificationThread(taskSize);
	}

}
