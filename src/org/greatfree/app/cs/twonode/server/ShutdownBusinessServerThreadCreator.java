package org.greatfree.app.cs.twonode.server;

import org.greatfree.app.cs.twonode.message.ShutdownBusinessServerNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 07/27/2018, Bing Li
class ShutdownBusinessServerThreadCreator implements NotificationThreadCreatable<ShutdownBusinessServerNotification, ShutdownBusinessServerThread>
{

	@Override
	public ShutdownBusinessServerThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownBusinessServerThread(taskSize);
	}

}
