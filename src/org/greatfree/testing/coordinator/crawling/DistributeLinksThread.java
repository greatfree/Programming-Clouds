package org.greatfree.testing.coordinator.crawling;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.coordinator.memorizing.MemoryCoordinator;
import org.greatfree.testing.message.CrawledLinksNotification;

/*
 * The thread gets the crawled links and distribute them to the distributed memory servers. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class DistributeLinksThread extends NotificationQueue<CrawledLinksNotification>
{
	/*
	 * Initialize the thread. The argument, taskSize, is used to limit the count of tasks to be queued. 11/28/2014, Bing Li
	 */
	public DistributeLinksThread(int taskSize)
	{
		super(taskSize);
	}
	
	/*
	 * Once if a crawled links notification is received, it is processed concurrently as follows. 11/28/2014, Bing Li
	 */
	public void run()
	{
		// Declare an instance of CrawledLinksNotification. 11/28/2014, Bing Li
		CrawledLinksNotification notification;
		// The thread always runs until it is shutdown by the NotificationDispatcher. 11/28/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/28/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 11/28/2014, Bing Li
					notification = this.dequeue();
					// Distribute the crawled links to distributed memory servers. 11/28/2014, Bing Li
					MemoryCoordinator.COORDINATOR().distributeCrawledLink(notification.getLinks());
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
