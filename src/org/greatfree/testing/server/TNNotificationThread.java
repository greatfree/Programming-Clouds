package org.greatfree.testing.server;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.message.TNNotification;

// Created: 04/10/2020, Bing Li
class TNNotificationThread extends NotificationQueue<TNNotification>
{

	public TNNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		TNNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					System.out.println(notification.getMessage());
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 01/20/2016, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

}
