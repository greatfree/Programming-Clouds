package com.greatfree.testing.crawlserver;

import com.greatfree.concurrency.BoundNotificationQueue;
import com.greatfree.reuse.MulticastMessageDisposer;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.StartCrawlMultiNotification;

/*
 * The thread processes the instances of StartCrawlMultiNotification from the coordinator. Since the notification is shared by the one that needs to forward it, both of them are derived from BoundNotificationQueue for synchronization. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public class StartCrawlThread extends BoundNotificationQueue<StartCrawlMultiNotification, MulticastMessageDisposer<StartCrawlMultiNotification>>
{
	/*
	 * Initialize the thread. 11/26/2014, Bing Li
	 * 
	 * In the constructor, the parameters are explained as follows.
	 * 
	 * 		int taskSize: the size of the notification queue.
	 * 
	 * 		String dispatcherKey: the key of the dispatcher which manages the thread. It is the thread key that shares the notification in the binder below.
	 * 
	 * 		MulticastMessageDisposer<StartCrawlMultiNotification> binder: the binder that synchronizes the relevant threads that share the notification and disposes the notification after the sharing is ended.
	 * 
	 */
	public StartCrawlThread(int taskSize, String dispatcherKey, MulticastMessageDisposer<StartCrawlMultiNotification> binder)
	{
		super(taskSize, dispatcherKey, binder);
	}

	/*
	 * The thread to process notifications asynchronously. 11/26/2014, Bing Li
	 */
	public void run()
	{
		// Declare a notification instance of StartCrawlMultiNotification. 11/26/2014, Bing Li
		StartCrawlMultiNotification notification;
		// The thread always runs until it is shutdown by the BoundNotificationDispatcher. 11/26/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/26/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 11/26/2014, Bing Li
					notification = this.getNotification();
					// Start the crawling. 11/26/2014, Bing Li
					CrawlServer.CRAWL().startCrawl();
					// Notify the binder that the notification is consumed by the thread. 11/26/2014, Bing Li
					this.bind(super.getDispatcherKey(), notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 11/26/2014, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
