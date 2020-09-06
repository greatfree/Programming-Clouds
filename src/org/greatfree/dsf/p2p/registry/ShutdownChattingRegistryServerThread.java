package org.greatfree.dsf.p2p.registry;

import java.io.IOException;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.ServerStatus;

/*
 * The thread deals with shutting down notification from the administrator. 04/30/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
class ShutdownChattingRegistryServerThread extends NotificationQueue<ShutdownServerNotification>
{

	public ShutdownChattingRegistryServerThread(int taskSize)
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
					notification = this.getNotification();
					ServerStatus.FREE().setShutdown();

					// Stop the registry server. 06/04/2017, Bing Li
					RegistryServer.PEER().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);

					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException | ClassNotFoundException | RemoteReadException e)
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
