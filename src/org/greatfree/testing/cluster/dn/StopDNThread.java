package org.greatfree.testing.cluster.dn;

import java.io.IOException;

import org.greatfree.admin.AdminConfig;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.message.StopDNMultiNotification;
import org.greatfree.util.ServerStatus;

/*
 * The thread processes the instances of StopDNMultiNotification from the coordinator. Since the notification is shared by the one that needs to forward it, both of them are derived from BoundNotificationQueue for synchronization. 11/26/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class StopDNThread extends NotificationQueue<StopDNMultiNotification>
{
	/*
	 * Initialize the thread. The argument, taskSize, is used to limit the count of tasks to be queued. 11/27/2014, Bing Li
	 */
	public StopDNThread(int taskSize)
	{
		super(taskSize);
	}

	/*
	 * The thread to process notifications asynchronously. 11/26/2014, Bing Li
	 */
	@Override
	public void run()
	{
		// Declare an instance of StopDNMultiNotification. 11/27/2014, Bing Li
		StopDNMultiNotification notification;
		// The thread always runs until it is shutdown by the NotificationDispatcher. 11/27/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/27/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 11/27/2014, Bing Li
					notification = this.getNotification();
					try
					{
						// Disseminate the stop notification to all of the current DN's children nodes. 11/27/2014, Bing Li
						DNMulticastor.CLUSTER().disseminateStopDNMultiNotification(notification);
					}
					catch (InstantiationException e)
					{
						e.printStackTrace();
					}
					catch (IllegalAccessException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					try
					{
						// Set the DN as shutdown. 01/30/2016, Bing Li
						ServerStatus.FREE().setShutdown(AdminConfig.DN);
						// Stop the current crawler. 11/27/2014, Bing Li
						DN.CLUSTER().stop();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					// Dispose the notification. 11/27/2014, Bing Li
					this.disposeMessage(notification);
					return;
				}
				catch (InterruptedException e)
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
