package edu.greatfree.threetier.coordinator;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

import edu.greatfree.threetier.message.FrontNotification;

// Created: 07/06/2018, Bing Li
class FrontNotificationThread extends NotificationQueue<FrontNotification>
{

	public FrontNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		FrontNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					System.out.println("Notification: " + notification.getNotification());
					Coordinator.CPS().notify(notification.getNotification());
					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException e)
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
