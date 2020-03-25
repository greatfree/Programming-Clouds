package org.greatfree.app.cps.terminal;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 08/14/2018, Bing Li
public class ShutdownDBServerThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownDBServerThread>
{

	@Override
	public ShutdownDBServerThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownDBServerThread(taskSize);
	}

}
