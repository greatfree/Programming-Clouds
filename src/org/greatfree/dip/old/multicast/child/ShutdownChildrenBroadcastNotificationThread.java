package org.greatfree.dip.old.multicast.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.MulticastConfig;
import org.greatfree.dip.multicast.message.OldShutdownChildrenBroadcastNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.ServerStatus;

/*
 * The thread processes the instances of ShutdownChildrenBroadcastNotification from the coordinator. Since the notification is shared by the one that needs to forward it, both of them are derived from BoundNotificationQueue for synchronization. 11/26/2014, Bing Li
 */

// Created: 05/19/2017, Bing Li
class ShutdownChildrenBroadcastNotificationThread extends NotificationQueue<OldShutdownChildrenBroadcastNotification>
{
	public ShutdownChildrenBroadcastNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	/*
	 * The thread to process notifications asynchronously. 11/26/2014, Bing Li
	 */
	@Override
	public void run()
	{
		// Declare a notification instance of ShutdownChildrenBroadcastNotification. 11/26/2014, Bing Li
		OldShutdownChildrenBroadcastNotification notification;
		// The thread always runs until it is shutdown by the BoundNotificationDispatcher. 11/26/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/26/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 11/26/2014, Bing Li
					notification = this.getNotification();

					// Disseminate the notification of ShutdownChildrenBroadcastNotification among children. 11/27/2014, Bing Li
					try
					{
						ClusterChildSingleton.CLUSTER().broadcastShutdownNotify(notification, MulticastConfig.SUB_BRANCH_COUNT);
					}
					catch (InstantiationException | IllegalAccessException e)
					{
						e.printStackTrace();
					}

					// Set the shutdown status. 05/20/2017, Bing Li
					ServerStatus.FREE().setShutdown();

					// Shutdown the child. 05/19/2017, Bing Li
					try
					{
						ClusterChildSingleton.CLUSTER().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
					}
					catch (ClassNotFoundException | RemoteReadException e)
					{
						e.printStackTrace();
					}

					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException e)
				{
					e.printStackTrace();
				}
				
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 11/26/2014, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
