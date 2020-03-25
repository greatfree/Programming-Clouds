package org.greatfree.testing.crawlserver;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.message.StopCrawlMultiNotification;

/*
 * The thread receives stopping crawling notification from the coordinator. It transfers the notification to all of its children and then stop itself. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StopCrawlThread extends NotificationQueue<StopCrawlMultiNotification>
{
	/*
	 * Initialize the thread. The argument, taskSize, is used to limit the count of tasks to be queued. 11/27/2014, Bing Li
	 */
	public StopCrawlThread(int taskSize)
	{
		super(taskSize);
	}

	/*
	 * Once if the stop crawling notification is received, it is processed concurrently as follows. 11/27/2014, Bing Li
	 */
	public void run()
	{
		// Declare an instance of StopCrawlMultiNotification. 11/27/2014, Bing Li
		StopCrawlMultiNotification notification;
		// The thread always runs until it is shutdown by the NotificationDispatcher. 11/27/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/27/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 11/27/2014, Bing Li
					notification = this.getNotification();
					try
					{
						// Disseminate the stop notification to all of the current crawler's children nodes. 11/27/2014, Bing Li
						CrawlerMulticastor.CRAWLER().disseminateStopCrawlNotificationAmongSubCrawlServers(notification);
					}
					catch (InstantiationException e)
					{
						e.printStackTrace();
					}
					catch (IllegalAccessException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					try
					{
						// Stop the current crawler. 11/27/2014, Bing Li
						CrawlServer.CRAWL().stop();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					// Dispose the notification. 11/27/2014, Bing Li
					this.disposeMessage(notification);
					return;
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
