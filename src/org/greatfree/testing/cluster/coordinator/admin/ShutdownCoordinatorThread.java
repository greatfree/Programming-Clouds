package org.greatfree.testing.cluster.coordinator.admin;

import java.io.IOException;

import org.greatfree.admin.AdminConfig;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.cluster.coordinator.Coordinator;
import org.greatfree.testing.message.ShutdownCoordinatorNotification;
import org.greatfree.util.ServerStatus;

/*
 * The thread shuts down the coordinator to after receiving the notification, ShutdownCoordinatorNotification. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class ShutdownCoordinatorThread extends NotificationQueue<ShutdownCoordinatorNotification>
{
	/*
	 * Initialize the thread. 11/27/2014, Bing Li
	 */
	public ShutdownCoordinatorThread(int taskSize)
	{
		super(taskSize);
	}

	/*
	 * Process the notification concurrently. 11/27/2014, Bing Li
	 */
	@Override
	public void run()
	{
		// The instance of ShutdownCoordinatorNotification. 11/27/2014, Bing Li
		ShutdownCoordinatorNotification notification;
		// The thread always runs until it is shutdown by the NotificationDispatcher. 11/27/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/27/2014, Bing Li
			while (!this.isEmpty())
			{
				// Dequeue the notification. 11/27/2014, Bing Li
				try
				{
					notification = this.getNotification();
					// Set the coordinator as shutdown. 01/30/2016, Bing Li
					ServerStatus.FREE().setShutdown(AdminConfig.COORDINATOR);
					Coordinator.COORDINATOR().stop();
					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
