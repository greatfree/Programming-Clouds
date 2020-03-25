package org.greatfree.dip.streaming.unicast.root;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 03/22/2020, Bing Li
class ShutdownServerThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownServerThread>
{

	@Override
	public ShutdownServerThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownServerThread(taskSize);
	}

}
