package com.greatfree.testing.client;

import com.greatfree.concurrency.NotificationQueue;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.NodeKeyNotification;
import com.greatfree.util.NodeID;

/*
 * The thread starts to run when a node key notification is received. It keeps its unique key and registers itself to the server with the key. 11/09/2014, Bing Li
 */

// Created: 11/09/2014, Bing Li
public class RegisterThread extends NotificationQueue<NodeKeyNotification>
{
	/*
	 * Initialize the thread. The argument, taskSize, is used to limit the count of tasks to be queued. 11/09/2014, Bing Li
	 */
	public RegisterThread(int taskSize)
	{
		super(taskSize);
	}

	/*
	 * Once if a node key notification is received, it is processed concurrently as follows. 11/09/2014, Bing Li
	 */
	public void run()
	{
		// Declare an instance of NodeKeyNotification. 11/09/2014, Bing Li
		NodeKeyNotification notification;
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
					// Set the client key. 11/09/2014, Bing Li
					NodeID.DISTRIBUTED().setKey(notification.getNodeKey());
					// Register the client after getting the key. 11/09/2014, Bing Li
					ClientEventer.NOTIFY().register();
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
