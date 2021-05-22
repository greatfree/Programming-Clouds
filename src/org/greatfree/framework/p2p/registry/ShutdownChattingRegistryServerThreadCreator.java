package org.greatfree.framework.p2p.registry;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 04/30/2017, Bing Li
class ShutdownChattingRegistryServerThreadCreator implements NotificationQueueCreator<ShutdownServerNotification, ShutdownChattingRegistryServerThread>
{

	@Override
	public ShutdownChattingRegistryServerThread createInstance(int taskSize)
	{
		return new ShutdownChattingRegistryServerThread(taskSize);
	}

}
