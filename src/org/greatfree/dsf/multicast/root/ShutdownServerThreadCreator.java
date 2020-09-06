package org.greatfree.dsf.multicast.root;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 08/28/2018, Bing Li
class ShutdownServerThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownServerThread>
{

	@Override
	public ShutdownServerThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownServerThread(taskSize);
	}

}
