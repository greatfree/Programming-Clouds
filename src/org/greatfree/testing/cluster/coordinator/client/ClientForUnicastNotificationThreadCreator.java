package org.greatfree.testing.cluster.coordinator.client;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.ClientForUnicastNotification;

/*
 * The creator here attempts to create instances of ClientForBroadcastNotificationThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/27/2014, Bing Li
 */

// Created: 11/21/2016, Bing Li
public class ClientForUnicastNotificationThreadCreator implements NotificationQueueCreator<ClientForUnicastNotification, ClientForUnicastNotificationThread>
{

	@Override
	public ClientForUnicastNotificationThread createInstance(int taskSize)
	{
		return new ClientForUnicastNotificationThread(taskSize);
	}
}
