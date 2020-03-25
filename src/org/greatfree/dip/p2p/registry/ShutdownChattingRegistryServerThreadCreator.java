package org.greatfree.dip.p2p.registry;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 04/30/2017, Bing Li
class ShutdownChattingRegistryServerThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownChattingRegistryServerThread>
{

	@Override
	public ShutdownChattingRegistryServerThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownChattingRegistryServerThread(taskSize);
	}

}
