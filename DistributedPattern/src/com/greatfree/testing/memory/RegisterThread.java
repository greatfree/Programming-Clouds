package com.greatfree.testing.memory;

import com.greatfree.concurrency.NotificationQueue;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.NodeKeyNotification;
import com.greatfree.util.NodeID;

/*
 * After getting a notification from the coordinator, it denotes that the coordinator is ready such that the memory server can register itself to the coordinator and prepare for the coming storing. 11/28/2014, Bing Li
 * 
 * The notification contains the key for the memory server. The key is the identification of the memory server. The coordinator uses the ID to manage the cluster of memory servers. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class RegisterThread extends NotificationQueue<NodeKeyNotification>
{
	/*
	 * Initialize the thread. The argument, taskSize, is used to limit the count of tasks to be queued. 11/28/2014, Bing Li
	 */
	public RegisterThread(int taskSize)
	{
		super(taskSize);
	}
	
	/*
	 * Once if a node key notification is received, it is processed concurrently as follows. 11/28/2014, Bing Li
	 */
	public void run()
	{
		// Declare an instance of NodeKeyNotification. 11/28/2014, Bing Li
		NodeKeyNotification notification;
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
					// Set the crawler key. 11/28/2014, Bing Li
					NodeID.DISTRIBUTED().setKey(notification.getKey());
					// Register the memory server after getting the key. 11/28/2014, Bing Li
					MemoryEventer.NOTIFY().register();
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
