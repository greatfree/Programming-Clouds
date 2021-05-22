package org.greatfree.testing.cluster.coordinator.client;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.cluster.coordinator.CoordinatorMulticastNotifier;
import org.greatfree.testing.message.ClientForBroadcastNotification;

/*
 * The thread is responsible for determining whether the received notification should be broadcast or not. 11/26/2014, Bing Li
 */

// Created: 11/21/2016, Bing Li
public class ClientForBroadcastNotificationThread extends NotificationQueue<ClientForBroadcastNotification>
{
	/*
	 * Initialize the thread. 11/26/2014, Bing Li
	 */
	public ClientForBroadcastNotificationThread(int taskSize)
	{
		super(taskSize);
	}
	
	/*
	 * Process the notification concurrently. 11/26/2014, Bing Li
	 */
	public void run()
	{
		// The instance of ClientForBroadcastNotification. 11/26/2014, Bing Li
		ClientForBroadcastNotification notification;
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
					
					// Broadcasting must be added here ...
					CoordinatorMulticastNotifier.COORDINATE().disseminateBroadcastNotification(notification.getMessage());

					this.disposeMessage(notification);
				}
				catch (InterruptedException | InstantiationException | IllegalAccessException | IOException e)
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
