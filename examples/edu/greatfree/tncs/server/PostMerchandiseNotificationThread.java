package edu.greatfree.tncs.server;

import org.greatfree.concurrency.reactive.NotificationQueue;

import edu.greatfree.tncs.message.PostMerchandiseNotification;

// Created: 05/18/2019, Bing Li
class PostMerchandiseNotificationThread extends NotificationQueue<PostMerchandiseNotification>
{
	public PostMerchandiseNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PostMerchandiseNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					System.out.println("ID = " + notification.getMerchandise().getMerchandiseID());
					System.out.println("Name = " + notification.getMerchandise().getMerchandiseName());
					System.out.println("Description = " + notification.getMerchandise().getDescription());
					System.out.println("Price = " + notification.getMerchandise().getPrice());
					System.out.println("In Stock = " + notification.getMerchandise().getInStock());
					System.out.println("Manufacturer = " + notification.getMerchandise().getManufacturer());
					System.out.println("Shipping Manner = " + notification.getMerchandise().getShippingManner());
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
