package org.greatfree.app.cps.coordinator;

import java.io.IOException;

import org.greatfree.app.cps.message.OrderNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

// Created: 08/14/2018, Bing Li
public class OrderNotificationThread extends NotificationQueue<OrderNotification>
{

	public OrderNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		OrderNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					System.out.println("Order notification: " + notification.getMerchandise() + ", " + notification.getCount());
					VendorCoordinator.CPS().notify(notification.getMerchandise(), notification.getCount());
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
