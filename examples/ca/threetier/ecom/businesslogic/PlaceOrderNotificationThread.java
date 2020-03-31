package ca.threetier.ecom.businesslogic;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

import ca.dp.tncs.message.PlaceOrderNotification;

// Created: 03/09/2020, Bing Li
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
					System.out.println("PlaceOrderNotification: " + notification.getClientName() + ", " + notification.getBookName() + ", " + notification.getBookCount() + ", " + notification.getPayment());
					BusinessLogic.CPS().notify(notification);
					System.out.println("PlaceOrderNotification is forwarded ...");
					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException e)
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
