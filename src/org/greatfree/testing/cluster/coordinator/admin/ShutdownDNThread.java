package org.greatfree.testing.cluster.coordinator.admin;

import java.io.IOException;

import org.greatfree.admin.AdminConfig;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.message.ShutdownDNNotification;
import org.greatfree.util.ServerStatus;

/*
 * The thread disseminates the multicasting notification to all of the DN to shut down them. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class ShutdownDNThread extends NotificationQueue<ShutdownDNNotification>
{
	/*
	 * Initialize the thread. 11/27/2014, Bing Li
	 */
	public ShutdownDNThread(int taskSize)
	{
		super(taskSize);
	}

	/*
	 * Process the notification concurrently. 11/27/2014, Bing Li
	 */
	@Override
	public void run()
	{
		// The instance of ShutdownDNNotification. 11/27/2014, Bing Li
		ShutdownDNNotification notification;
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
					// Set the DN as shutdown. 01/30/2016, Bing Li
					ServerStatus.FREE().setShutdown(AdminConfig.DN);
					// Disseminate the notification to demand all of the DNs to stop them. 11/27/2014, Bing Li
					AdminMulticastor.ADMIN().disseminateStopDNs();
					this.disposeMessage(notification);
				}
				catch (InterruptedException | InstantiationException | IllegalAccessException | IOException e)
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
