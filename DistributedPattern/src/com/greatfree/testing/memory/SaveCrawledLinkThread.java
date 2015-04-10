package com.greatfree.testing.memory;

import com.greatfree.concurrency.NotificationQueue;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.AddCrawledLinkNotification;

/*
 * The thread gets the crawled link and save it in the local server. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class SaveCrawledLinkThread extends NotificationQueue<AddCrawledLinkNotification>
{
	/*
	 * Initialize the thread. The argument, taskSize, is used to limit the count of tasks to be queued. 11/28/2014, Bing Li
	 */
	public SaveCrawledLinkThread(int taskSize)
	{
		super(taskSize);
	}
	
	/*
	 * Once if a crawled link notification is received, it is processed concurrently as follows. 11/28/2014, Bing Li
	 */
	public void run()
	{
		// Declare an instance of AddCrawledLinkNotification. 11/28/2014, Bing Li
		AddCrawledLinkNotification notification;
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
					// Save the crawled link into the memory cache. 11/28/2014, Bing Li
					LinkPond.STORE().save(notification.getLink());
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
