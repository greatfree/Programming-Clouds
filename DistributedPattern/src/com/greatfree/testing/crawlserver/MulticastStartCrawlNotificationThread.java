package com.greatfree.testing.crawlserver;

import java.io.IOException;

import com.greatfree.concurrency.BoundNotificationQueue;
import com.greatfree.reuse.MulticastMessageDisposer;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.StartCrawlMultiNotification;

/*
 * The thread processes the notification of StartCrawlMultiNotification concurrently. Since it shares the notifications with other threads, i.e., StartCrawlThread, it must have the mechanism of synchronization. Therefore, it derives the BoundNotificationQueue rather than NotificationQueue. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class MulticastStartCrawlNotificationThread extends BoundNotificationQueue<StartCrawlMultiNotification, MulticastMessageDisposer<StartCrawlMultiNotification>>
{
	/*
	 * Initialize the thread. 11/27/2014, Bing Li
	 * 
	 * In the constructor, the parameters are explained as follows.
	 * 
	 * 		int taskSize: the size of the notification queue.
	 * 
	 * 		String dispatcherKey: the key of the dispatcher which manages the thread. It is the thread key that shares the notification in the binder below.
	 * 
	 * 		MulticastMessageDisposer<StartCrawlMultiNotification> disposer: the disposer that synchronizes the relevant threads that share the notification and disposes the notification after the sharing is ended.
	 * 
	 */
	public MulticastStartCrawlNotificationThread(int taskSize, String dispatcherKey, MulticastMessageDisposer<StartCrawlMultiNotification> disposer)
	{
		super(taskSize, dispatcherKey, disposer);
	}
	
	/*
	 * The thread to process notifications asynchronously. 11/27/2014, Bing Li
	 */
	public void run()
	{
		// Declare a notification instance of StartCrawlMultiNotification. 11/27/2014, Bing Li
		StartCrawlMultiNotification notification;
		// The thread always runs until it is shutdown by the BoundNotificationDispatcher. 11/27/2014, Bing Li
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
						// Disseminate the notification of StartCrawlNotification among children crawlers. 11/27/2014, Bing Li
						CrawlerMulticastor.CRAWLER().disseminateStartCrawlNotificationAmongSubCrawlServers(notification);
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
					// Notify the binder that the notification is consumed by the thread. 11/27/2014, Bing Li
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
