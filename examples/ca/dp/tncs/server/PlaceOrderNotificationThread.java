package ca.dp.tncs.server;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

import ca.dp.tncs.message.PlaceOrderNotification;

// Created: 02/22/2020, Bing Li
class PlaceOrderNotificationThread extends NotificationQueue<PlaceOrderNotification>
{

	public PlaceOrderNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PlaceOrderNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					System.out.println("Place order: " + notification.getClientName() + ", " + notification.getBookName() + ", " + notification.getBookCount() + ", " + notification.getPayment());
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
