package org.greatfree.framework.multicast.rp.root;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 10/21/2018, Bing Li
public class ShutdownServerThreadCreator implements NotificationQueueCreator<ShutdownServerNotification, ShutdownServerThread>
{

	@Override
	public ShutdownServerThread createInstance(int taskSize)
	{
		return new ShutdownServerThread(taskSize);
	}

}
