package org.greatfree.framework.multicast.root;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 08/28/2018, Bing Li
class ShutdownServerThreadCreator implements NotificationQueueCreator<ShutdownServerNotification, ShutdownServerThread>
{

	@Override
	public ShutdownServerThread createInstance(int taskSize)
	{
		return new ShutdownServerThread(taskSize);
	}

}
