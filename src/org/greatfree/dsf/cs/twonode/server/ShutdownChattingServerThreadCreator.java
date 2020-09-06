package org.greatfree.dsf.cs.twonode.server;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 05/13/2018, Bing Li
public class ShutdownChattingServerThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownChattingServerThread>
{

	@Override
	public ShutdownChattingServerThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownChattingServerThread(taskSize);
	}

}
