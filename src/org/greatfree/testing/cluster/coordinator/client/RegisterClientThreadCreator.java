package org.greatfree.testing.cluster.coordinator.client;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.RegisterClientNotification;

/*
 * The code here attempts to create instances of SetInputStreamThread. 11/30/2014, Bing Li
 */

// Created: 11/30/2014, Bing Li
public class RegisterClientThreadCreator implements NotificationQueueCreator<RegisterClientNotification, RegisterClientThread>
{
	@Override
	public RegisterClientThread createInstance(int taskSize)
	{
		return new RegisterClientThread(taskSize);
	}
}
