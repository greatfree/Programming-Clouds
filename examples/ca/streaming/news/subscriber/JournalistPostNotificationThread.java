package ca.streaming.news.subscriber;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.child.ChildMulticastor;
import org.greatfree.dip.streaming.StreamConfig;
import org.greatfree.dip.streaming.subscriber.SubscriberDB;

import ca.streaming.news.message.JournalistPostNotification;

// Created: 04/03/2020, Bing Li
class JournalistPostNotificationThread extends NotificationQueue<JournalistPostNotification>
{

	public JournalistPostNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		JournalistPostNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					ChildMulticastor.CHILD().asyncNotify(notification);
					System.out.println("JournalistPostNotificationThread: stream received: " + notification.getNews());
					SubscriberDB.DB().save(notification.getNews().getMessage());
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					System.out.println(StreamConfig.ONE_SUBSCRIBER_LEAVE);
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
