package org.greatfree.testing.server;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;

// Created: 04/10/2020, Bing Li
class ShutdownServerNotificationThread extends NotificationQueue<ShutdownServerNotification>
{

	public ShutdownServerNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}

}
