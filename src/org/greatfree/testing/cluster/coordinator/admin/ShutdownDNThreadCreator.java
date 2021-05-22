package org.greatfree.testing.cluster.coordinator.admin;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.ShutdownDNNotification;

/*
 * The creator here attempts to create instances of ShutdownDNThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class ShutdownDNThreadCreator implements NotificationQueueCreator<ShutdownDNNotification, ShutdownDNThread>
{

	@Override
	public ShutdownDNThread createInstance(int taskSize)
	{
		return new ShutdownDNThread(taskSize);
	}

}
