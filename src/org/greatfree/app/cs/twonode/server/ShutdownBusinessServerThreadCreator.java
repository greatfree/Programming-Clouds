package org.greatfree.app.cs.twonode.server;

import org.greatfree.app.cs.twonode.message.ShutdownBusinessServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 07/27/2018, Bing Li
class ShutdownBusinessServerThreadCreator implements NotificationQueueCreator<ShutdownBusinessServerNotification, ShutdownBusinessServerThread>
{

	@Override
	public ShutdownBusinessServerThread createInstance(int taskSize)
	{
		return new ShutdownBusinessServerThread(taskSize);
	}

}
