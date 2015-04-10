package com.greatfree.testing.coordinator.crawling;

import com.greatfree.concurrency.NotificationQueue;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.UnregisterCrawlServerNotification;

/*
 * The thread processes the unregister notification from a crawling server concurrently. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class UnregisterCrawlServerThread extends NotificationQueue<UnregisterCrawlServerNotification>
{
	/*
	 * Initialize the thread. 11/27/2014, Bing Li
	 */
	public UnregisterCrawlServerThread(int taskSize)
	{
		super(taskSize);
	}
	
	/*
	 * Process the notification concurrently. 11/27/2014, Bing Li
	 */
	public void run()
	{
		// Declare an instance of UnregisterCrawlServerNotification. 11/28/2014, Bing Li
		UnregisterCrawlServerNotification notification;
		// The thread always runs until it is shutdown by the NotificationDispatcher. 11/28/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/28/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 11/28/2014, Bing Li
					notification = this.getNotification();
					// Unregister the crawler from the registry. 11/28/2014, Bing Li
					CrawlRegistry.COORDINATE().unregister(notification.getDCKey());
					// Dispose the notification. 11/28/2014, Bing Li
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 11/28/2014, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
