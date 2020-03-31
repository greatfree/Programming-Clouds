package edu.greatfree.p2p.registry;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import edu.greatfree.cs.multinode.message.ShutdownServerNotification;

// Created: 04/30/2017, Bing Li
class ShutdownChattingRegistryServerThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownChattingRegistryServerThread>
{

	@Override
	public ShutdownChattingRegistryServerThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownChattingRegistryServerThread(taskSize);
	}

}
