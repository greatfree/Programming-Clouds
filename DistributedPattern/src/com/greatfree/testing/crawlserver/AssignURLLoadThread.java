package com.greatfree.testing.crawlserver;

import com.greatfree.concurrency.NotificationQueue;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.data.URLValue;
import com.greatfree.testing.message.CrawlLoadNotification;

/*
 * The thread gets the crawling workload from the coordinator and injects them into the crawling scheduler. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class AssignURLLoadThread extends NotificationQueue<CrawlLoadNotification>
{
	/*
	 * Initialize the thread. The argument, taskSize, is used to limit the count of tasks to be queued. 11/28/2014, Bing Li
	 */
	public AssignURLLoadThread(int taskSize)
	{
		super(taskSize);
	}
	
	/*
	 * Once if a workload notification is received, it is processed concurrently as follows. 11/28/2014, Bing Li
	 */
	public void run()
	{
		// Declare an instance of CrawlLoadNotification. 11/28/2014, Bing Li
		CrawlLoadNotification notification;
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
					// Convert the instances of URLValue to HubURLs and place them into the crawling scheduler. 11/28/2014, Bing Li
					for (URLValue url : notification.getURLs().values())
					{
						CrawlScheduler.CRAWL().enqueue(new HubURL(url.getKey(), url.getURL(), url.getUpdatingPeriod()));
					}
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
