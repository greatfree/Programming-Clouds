package org.greatfree.framework.cps.cache.terminal.man;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 07/07/2018, Bing Li
public class ShutdownTerminalServerThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownTerminalServerThread>
{

	@Override
	public ShutdownTerminalServerThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownTerminalServerThread(taskSize);
	}

}
