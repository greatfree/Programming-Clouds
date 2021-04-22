package org.greatfree.framework.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.streaming.message.AddStreamNotification;

// Created: 03/19/2020, Bing Li
public class AddStreamNotificationThread extends NotificationQueue<AddStreamNotification>
{

	public AddStreamNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		AddStreamNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					StreamRegistry.PUBSUB().addStream(notification.getPublisher(), notification.getTopic());
					this.disposeMessage(notification);
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
