package org.greatfree.app.cps.coordinator;

import java.io.IOException;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;

// Created: 08/14/2018, Bing Li
public class ShutdownCordinatorThread extends NotificationQueue<ShutdownServerNotification>
{

	public ShutdownCordinatorThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ShutdownServerNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					VendorCoordinator.CPS().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
					this.disposeMessage(notification);
				}
				catch (InterruptedException | ClassNotFoundException | IOException | RemoteReadException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 01/20/2016, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
