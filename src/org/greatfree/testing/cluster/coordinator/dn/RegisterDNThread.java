package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.cluster.coordinator.Profile;
import org.greatfree.testing.message.RegisterClientNotification;

/*
 * The thread is responsible for determining whether to distribute the DN workload again or to start the DN jobs after receiving registration notifications. 11/26/2014, Bing Li
 */

// Created: 11/23/2016, Bing Li
public class RegisterDNThread extends NotificationQueue<RegisterClientNotification>
{
	/*
	 * Initialize the thread. 11/26/2014, Bing Li
	 */
	public RegisterDNThread(int taskSize)
	{
		super(taskSize);
	}
	
	/*
	 * Process the notification concurrently. 11/26/2014, Bing Li
	 */
	public void run()
	{
		// The instance of RegisterClientNotification. 11/26/2014, Bing Li
		RegisterClientNotification notification;
		// The thread always runs until it is shutdown by the NotificationDispatcher. 11/26/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/26/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 11/26/2014, Bing Li
					notification = this.dequeue();
					// Register the DN. 11/26/2014, Bing Li
					DNRegistry.COORDINATE().register(notification.getClientKey());
					// Check whether the count of registered DNs is equal to that of predefined ones. 11/26/2014, Bing Li
					if (DNRegistry.COORDINATE().getDNCount() == Profile.CONFIG().getClusterServerCount())
					{
						// Set the cluster ready. 11/23/2016, Bing Li
						DNRegistry.COORDINATE().setReady();
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
