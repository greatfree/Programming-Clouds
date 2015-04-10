package com.greatfree.testing.server;

import com.greatfree.concurrency.NotificationQueue;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.RegisterClientNotification;

/*
 * The thread receives registration notification from clients and keep their keys in the registry. 11/09/2014, Bing Li
 */

// Created: 11/08/2014, Bing Li
public class RegisterClientThread extends NotificationQueue<RegisterClientNotification>
{
	/*
	 * Initialize the thread. The argument, taskSize, is used to limit the count of tasks to be queued. 11/09/2014, Bing Li
	 */
	public RegisterClientThread(int taskSize)
	{
		super(taskSize);
	}
	
	/*
	 * Once if a client registration notification is received, it is processed concurrently as follows. 11/09/2014, Bing Li
	 */
	public void run()
	{
		// Declare an instance of RegisterClientNotification. 11/09/2014, Bing Li
		RegisterClientNotification notification;
		// The thread always runs until it is shutdown by the NotificationDispatcher. 11/09/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/09/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 11/09/2014, Bing Li
					notification = this.getNotification();
					// Register the client. 11/09/2014, Bing Li
					ClientRegistry.MANAGEMENT().register(notification.getClientKey());
					// Dispose the notification. 11/09/2014, Bing Li
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 11/09/2014, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
