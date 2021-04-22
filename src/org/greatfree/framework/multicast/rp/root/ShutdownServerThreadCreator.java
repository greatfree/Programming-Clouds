package org.greatfree.framework.multicast.rp.root;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 10/21/2018, Bing Li
public class ShutdownServerThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownServerThread>
{

	@Override
	public ShutdownServerThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownServerThread(taskSize);
	}

}
