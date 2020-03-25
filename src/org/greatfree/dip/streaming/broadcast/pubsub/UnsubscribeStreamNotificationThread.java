package org.greatfree.dip.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.streaming.message.UnsubscribeStreamNotification;

// Created: 03/19/2020, Bing Li
class UnsubscribeStreamNotificationThread extends NotificationQueue<UnsubscribeStreamNotification>
{

	public UnsubscribeStreamNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		UnsubscribeStreamNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					StreamRegistry.PUBSUB().unsubscribe(notification.getPublisher(), notification.getTopic(), notification.getSubscriber());
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
