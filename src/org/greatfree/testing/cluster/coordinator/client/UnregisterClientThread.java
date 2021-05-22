package org.greatfree.testing.cluster.coordinator.client;

import org.greatfree.client.ClientRegistry;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.message.UnregisterClientNotification;

/*
 * The thread processes the unregister notification from a client concurrently. 11/27/2014, Bing Li
 */

// Created: 11/21/2016, Bing Li
public class UnregisterClientThread extends NotificationQueue<UnregisterClientNotification>
{
	/*
	 * Initialize the thread. 11/27/2014, Bing Li
	 */
	public UnregisterClientThread(int taskSize)
	{
		super(taskSize);
	}

	public void run()
	{
		UnregisterClientNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					ClientRegistry.MANAGEMENT().unregister(notification.getClientKey());
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
