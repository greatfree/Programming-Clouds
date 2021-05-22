package org.greatfree.app.cps.terminal;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 08/14/2018, Bing Li
public class ShutdownDBServerThreadCreator implements NotificationQueueCreator<ShutdownServerNotification, ShutdownDBServerThread>
{

	@Override
	public ShutdownDBServerThread createInstance(int taskSize)
	{
		return new ShutdownDBServerThread(taskSize);
	}

}
