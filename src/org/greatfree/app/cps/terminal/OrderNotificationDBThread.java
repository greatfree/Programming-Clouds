package org.greatfree.app.cps.terminal;

import org.greatfree.app.cps.message.OrderNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

// Created: 08/14/2018, Bing Li
public class OrderNotificationDBThread extends NotificationQueue<OrderNotification>
{

	public OrderNotificationDBThread(int taskSize)
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
					System.out.println("The coordinator notification: " + notification.getMerchandise() + ", " + notification.getCount());
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

}
