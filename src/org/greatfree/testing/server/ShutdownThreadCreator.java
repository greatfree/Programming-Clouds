package org.greatfree.testing.server;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.ShutdownServerNotification;

/*
 * The code here attempts to create instances of ShutdownThread. 01/20/2016, Bing Li
 */

// Created: 01/20/2016, Bing Li
public class ShutdownThreadCreator implements NotificationQueueCreator<ShutdownServerNotification, ShutdownThread>
{
	@Override
	public ShutdownThread createInstance(int taskSize)
	{
		return new ShutdownThread(taskSize);
	}
}
