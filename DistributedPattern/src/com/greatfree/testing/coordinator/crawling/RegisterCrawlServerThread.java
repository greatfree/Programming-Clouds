package com.greatfree.testing.coordinator.crawling;

import java.io.IOException;

import com.greatfree.concurrency.NotificationQueue;
import com.greatfree.testing.coordinator.CoordinatorMulticastor;
import com.greatfree.testing.coordinator.Profile;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.RegisterCrawlServerNotification;

/*
 * The thread is responsible for determining whether to distribute the crawling workload again or to start the crawling after receiving registration notifications. 11/26/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class RegisterCrawlServerThread extends NotificationQueue<RegisterCrawlServerNotification>
{
	/*
	 * Initialize the thread. 11/26/2014, Bing Li
	 */
	public RegisterCrawlServerThread(int taskSize)
	{
		super(taskSize);
	}

	/*
	 * Process the notification concurrently. 11/26/2014, Bing Li
	 */
	public void run()
	{
		// The instance of RegisterCrawlServerNotification. 11/26/2014, Bing Li
		RegisterCrawlServerNotification notification;
		// The thread always runs until it is shutdown by the NotificationDispatcher. 11/26/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/26/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 11/26/2014, Bing Li
					notification = this.getNotification();
					// Register the crawler. 11/26/2014, Bing Li
					CrawlRegistry.COORDINATE().register(notification.getDCKey(), notification.getURLCount());
					// Check whether the count of registered crawlers is equal to that of predefined ones. 11/26/2014, Bing Li
					if (CrawlRegistry.COORDINATE().getCrawlDCCount() == Profile.CONFIG().getCrawlServerCount())
					{
						// If all of the crawlers are registered, it needs to check whether the total URLs those crawlers take is less than that of the ones to be crawled. In addition, it also needs to check if one crawler has no any URLs. 11/26/2014, Bing Li
						if (CrawlRegistry.COORDINATE().getTotalURLCount() < CrawlCoordinator.COORDINATOR().getURLCount() || CrawlRegistry.COORDINATE().shouldReset())
						{
							// Clear the count of registered URLs. 11/26/2014, Bing Li
							CrawlRegistry.COORDINATE().clearURLCount();
							// Distribute the URLs, crawling loads, to each registered clusters. 11/26/2014, Bing Li
							CrawlCoordinator.COORDINATOR().distributeCrawlLoads();
						}
						else
						{
							try
							{
								// Disseminate the notification to demand all of the registered crawlers to start crawling. 11/26/2014, Bing Li
								CoordinatorMulticastor.COORDINATE().disseminateStartCrawl();
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
						}
					}
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
