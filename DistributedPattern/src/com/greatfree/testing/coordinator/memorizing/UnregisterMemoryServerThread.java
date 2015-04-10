package com.greatfree.testing.coordinator.memorizing;

import com.greatfree.concurrency.NotificationQueue;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.UnregisterMemoryServerNotification;

/*
 * The thread processes the unregister notification from a memory server concurrently. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class UnregisterMemoryServerThread extends NotificationQueue<UnregisterMemoryServerNotification>
{
	/*
	 * Initialize the thread. 11/28/2014, Bing Li
	 */
	public UnregisterMemoryServerThread(int taskSize)
	{
		super(taskSize);
	}

	/*
	 * Process the notification concurrently. 11/28/2014, Bing Li
	 */
	public void run()
	{
		// Declare an instance of UnregisterMemoryServerNotification. 11/28/2014, Bing Li
		UnregisterMemoryServerNotification notification;
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
					// Unregister the memory server from the registry. 11/28/2014, Bing Li
					MemoryRegistry.COORDINATE().unregister(notification.getDCKey());
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
