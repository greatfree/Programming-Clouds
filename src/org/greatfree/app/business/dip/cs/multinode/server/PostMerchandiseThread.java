package org.greatfree.app.business.dip.cs.multinode.server;

import org.greatfree.chat.message.cs.business.PostMerchandiseNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

// Created: 12/04/2017, Bing Li
public class PostMerchandiseThread extends NotificationQueue<PostMerchandiseNotification>
{

	public PostMerchandiseThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PostMerchandiseNotification notification;
		// Check whether the thread is shutdown or not. 12/05/2017, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the message queue of the thread is empty or not. 12/05/2017, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Get the notification out from the message queue. 12/05/2017, Bing Li
					notification = this.dequeue();
					// Save the merchandise. 12/05/2017, Bing Li
					Businesses.postMerchandise(notification.getVendorKey(), notification.getMerchandise());
					// Dispose the notification. 12/09/2017, Bing Li
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for some time when the queue is empty. During the period and before the thread is killed, some new notifications might be received. If so, the thread can keep working. 02/15/2016, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
