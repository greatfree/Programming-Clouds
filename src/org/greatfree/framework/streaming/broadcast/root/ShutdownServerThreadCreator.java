package org.greatfree.framework.streaming.broadcast.root;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 03/19/2020, Bing Li
class ShutdownServerThreadCreator implements NotificationQueueCreator<ShutdownServerNotification, ShutdownServerThread>
{

	@Override
	public ShutdownServerThread createInstance(int taskSize)
	{
		return new ShutdownServerThread(taskSize);
	}

}
