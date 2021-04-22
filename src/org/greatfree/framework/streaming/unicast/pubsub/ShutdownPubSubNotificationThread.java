package org.greatfree.framework.streaming.unicast.pubsub;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.streaming.message.ShutdownPubSubNotification;

// Created: 03/23/2020, Bing Li
class ShutdownPubSubNotificationThread extends NotificationQueue<ShutdownPubSubNotification>
{

	public ShutdownPubSubNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ShutdownPubSubNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					PubSubServer.UNI_STREAM().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
					this.disposeMessage(notification);
				}
				catch (InterruptedException | ClassNotFoundException | IOException | RemoteReadException e)
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
