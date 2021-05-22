package org.greatfree.framework.cs.twonode.server;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 05/13/2018, Bing Li
public class ShutdownChattingServerThreadCreator implements NotificationQueueCreator<ShutdownServerNotification, ShutdownChattingServerThread>
{

	@Override
	public ShutdownChattingServerThread createInstance(int taskSize)
	{
		return new ShutdownChattingServerThread(taskSize);
	}

}
