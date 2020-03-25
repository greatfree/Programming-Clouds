package org.greatfree.dip.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.streaming.message.RemoveStreamNotification;

// Created: 03/19/2020, Bing Li
public class RemoveStreamNotificationThread extends NotificationQueue<RemoveStreamNotification>
{

	public RemoveStreamNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		RemoveStreamNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					StreamRegistry.PUBSUB().removeStream(notification.getPublisher(), notification.getTopic());
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
