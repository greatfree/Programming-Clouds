package org.greatfree.app.cs.twonode.server;

import org.greatfree.app.cs.twonode.message.OrderDecisionNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

// Created: 07/27/2018, Bing Li
class OrderDecisionNotificationThread extends NotificationQueue<OrderDecisionNotification>
{

	public OrderDecisionNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		OrderDecisionNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					System.out.println("Your decision is: " + notification.isDecided());
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
