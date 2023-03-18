package org.greatfree.testing.server;

import java.io.IOException;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;

// Created: 04/10/2020, Bing Li
class ShutdownServerNotificationThread extends NotificationQueue<ShutdownServerNotification>
{

	public ShutdownServerNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ShutdownServerNotification notification;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					UnaryPeer.CS().stop();
					this.disposeMessage(notification);
				}
				catch (InterruptedException | ClassNotFoundException | IOException | RemoteReadException | RemoteIPNotExistedException e)
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
