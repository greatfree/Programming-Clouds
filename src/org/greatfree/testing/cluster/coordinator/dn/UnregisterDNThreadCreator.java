package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.UnregisterClientNotification;

/*
 * The creator here attempts to create instances of UnregisterDNThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/28/2014, Bing Li
 */

// Created: 11/23/2016, Bing Li
public class UnregisterDNThreadCreator implements NotificationQueueCreator<UnregisterClientNotification, UnregisterDNThread>
{

	@Override
	public UnregisterDNThread createInstance(int taskSize)
	{
		return new UnregisterDNThread(taskSize);
	}

}
