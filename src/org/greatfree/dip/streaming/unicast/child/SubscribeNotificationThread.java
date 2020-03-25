package org.greatfree.dip.streaming.unicast.child;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.streaming.message.SubscribeNotification;

// Created: 03/23/2020, Bing Li
class SubscribeNotificationThread extends NotificationQueue<SubscribeNotification>
{

	public SubscribeNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SubscribeNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					StreamRegistry.UNICAST().addSubscriber(notification.getStreamKey(), notification.getSubscriber(), notification.getSubscriberIP());
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
