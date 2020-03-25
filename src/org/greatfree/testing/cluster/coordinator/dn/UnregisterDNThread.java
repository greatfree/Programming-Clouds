package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.message.UnregisterClientNotification;

/*
 * The thread processes the unregister notification from a DN server concurrently. 11/27/2014, Bing Li
 */

// Created: 11/23/2016, Bing Li
public class UnregisterDNThread extends NotificationQueue<UnregisterClientNotification>
{
	/*
	 * Initialize the thread. 11/27/2014, Bing Li
	 */
	public UnregisterDNThread(int taskSize)
	{
		super(taskSize);
	}
	
	/*
	 * Process the notification concurrently. 11/27/2014, Bing Li
	 */
	public void run()
	{
		// Declare an instance of UnregisterClientNotification. 11/28/2014, Bing Li
		UnregisterClientNotification notification;
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
					// Unregister the DN from the registry. 11/28/2014, Bing Li
					DNRegistry.COORDINATE().unregister(notification.getClientKey());
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
