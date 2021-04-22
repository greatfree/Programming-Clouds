package org.greatfree.framework.streaming.unicast.pubsub;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.streaming.Stream;
import org.greatfree.framework.streaming.broadcast.pubsub.StreamRegistry;
import org.greatfree.framework.streaming.message.UnsubscribeNotification;
import org.greatfree.framework.streaming.message.UnsubscribeStreamNotification;

// Created: 03/23/2020, Bing Li
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
					PubSubServer.UNI_STREAM().unsubscribe(new UnsubscribeNotification(Stream.generateKey(notification.getPublisher(), notification.getTopic()), notification.getSubscriber()));
					StreamRegistry.PUBSUB().unsubscribe(notification.getPublisher(), notification.getTopic(), notification.getSubscriber());
					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException e)
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
