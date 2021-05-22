package org.greatfree.app.cs.twonode.server;

import org.greatfree.app.cs.twonode.message.PostMerchandiseNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

// Created: 07/31/2018, Bing Li
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
					notification = this.dequeue();
					MerchandiseDB.CS().saveMerchandise(notification.getVendor(), notification.getMerchandise());
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
