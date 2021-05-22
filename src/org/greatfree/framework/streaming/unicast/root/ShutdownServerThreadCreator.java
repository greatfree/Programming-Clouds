package org.greatfree.framework.streaming.unicast.root;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 03/22/2020, Bing Li
class ShutdownServerThreadCreator implements NotificationQueueCreator<ShutdownServerNotification, ShutdownServerThread>
{

	@Override
	public ShutdownServerThread createInstance(int taskSize)
	{
		return new ShutdownServerThread(taskSize);
	}

}
