package ca.streaming.news.subscriber;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.child.ChildMulticastor;
import org.greatfree.dip.streaming.StreamConfig;
import org.greatfree.dip.streaming.subscriber.SubscriberDB;

import ca.streaming.news.message.NewsFeedNotification;

// Created: 04/03/2020, Bing Li
class NewsFeedNotificationThread extends NotificationQueue<NewsFeedNotification>
{

	public NewsFeedNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		NewsFeedNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					ChildMulticastor.CHILD().asyncNotify(notification);
					System.out.println("NewsFeedNotificationThread: stream received: " + notification.getPost());
					SubscriberDB.DB().save(notification.getPost().getMessage());
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
