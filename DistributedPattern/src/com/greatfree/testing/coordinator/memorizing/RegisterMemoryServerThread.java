package com.greatfree.testing.coordinator.memorizing;

import com.greatfree.concurrency.NotificationQueue;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.RegisterMemoryServerNotification;

/*
 * The thread is responsible for registering the distributed memory nodes for data storing. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class RegisterMemoryServerThread extends NotificationQueue<RegisterMemoryServerNotification>
{
	/*
	 * Initialize the thread. 11/28/2014, Bing Li
	 */
	public RegisterMemoryServerThread(int taskSize)
	{
		super(taskSize);
	}
	
	/*
	 * Process the notification concurrently. 11/28/2014, Bing Li
	 */
	public void run()
	{
		// The instance of RegisterMemoryServerNotification. 11/28/2014, Bing Li
		RegisterMemoryServerNotification notification;
		// The thread always runs until it is shutdown by the NotificationDispatcher. 11/28/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/28/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 11/29/2014, Bing Li
					notification = this.getNotification();
					// Register the memory server. 11/29/2014, Bing Li
					MemoryRegistry.COORDINATE().register(notification.getDCKey());
					// Dispose the notification. 11/29/2014, Bing Li
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing requests are processed. 11/29/2014, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
