package edu.greatfree.cs.multinode.server;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import edu.greatfree.cs.multinode.message.ShutdownServerNotification;

// Created: 04/18/2017, Bing Li
class ShutdownChattingServerThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownChattingServerThread>
{

	@Override
	public ShutdownChattingServerThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownChattingServerThread(taskSize);
	}

}
