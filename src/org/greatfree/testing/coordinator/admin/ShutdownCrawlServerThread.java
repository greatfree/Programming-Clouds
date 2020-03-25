package org.greatfree.testing.coordinator.admin;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.message.ShutdownCrawlServerNotification;

/*
 * The thread disseminates the multicasting notification to all of the clusters to stop the crawling procedure. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class ShutdownCrawlServerThread extends NotificationQueue<ShutdownCrawlServerNotification>
{
	/*
	 * Initialize the thread. 11/27/2014, Bing Li
	 */
	public ShutdownCrawlServerThread(int taskSize)
	{
		super(taskSize);
	}
	
	/*
	 * Process the notification concurrently. 11/27/2014, Bing Li
	 */
	public void run()
	{
		// The instance of ShutdownCrawlServerNotification. 11/27/2014, Bing Li
		ShutdownCrawlServerNotification notification;
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
						// Disseminate the notification to demand all of the crawlers to stop crawling. 11/27/2014, Bing Li
						AdminMulticastor.ADMIN().disseminateStopCrawl();
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
