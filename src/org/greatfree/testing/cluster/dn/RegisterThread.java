package org.greatfree.testing.cluster.dn;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.message.NodeKeyNotification;
import org.greatfree.util.NodeID;

/*
 * After getting a notification from the coordinator, it denotes that the coordinator is ready such that the DN can register itself to the coordinator. 11/25/2014, Bing Li
 * 
 * The notification contains the key for the DN. The key is the identification of the DN. The coordinator uses the ID to manage the cluster of DNs. 11/25/2014, Bing Li
 */

// Created: 11/23/2016, Bing Li
public class RegisterThread extends NotificationQueue<NodeKeyNotification>
{
	/*
	 * Initialize the thread. The argument, taskSize, is used to limit the count of tasks to be queued. 11/25/2014, Bing Li
	 */
	public RegisterThread(int taskSize)
	{
		super(taskSize);
	}

	/*
	 * Once if a node key notification is received, it is processed concurrently as follows. 11/25/2014, Bing Li
	 */
	public void run()
	{
		// Declare an instance of NodeKeyNotification. 11/25/2014, Bing Li
		NodeKeyNotification notification;
		// The thread always runs until it is shutdown by the NotificationDispatcher. 11/25/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/25/2014, Bing Li
			while (!this.isEmpty())
			{
				// Dequeue the notification. 11/25/2014, Bing Li
				try
				{
					notification = this.getNotification();
					// Set the crawler key. 11/25/2014, Bing Li
					NodeID.DISTRIBUTED().setKey(notification.getKey());
					// Register the DN after getting the key. 11/25/2014, Bing Li
					DNEventer.NOTIFY().register();
					// Dispose the notification. 11/25/2014, Bing Li
					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 11/25/2014, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
