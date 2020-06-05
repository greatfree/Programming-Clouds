package ca.streaming.news.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.streaming.Stream;
import org.greatfree.dip.streaming.StreamConfig;
import org.greatfree.dip.streaming.broadcast.child.RootMulticastor;
import org.greatfree.dip.streaming.unicast.child.StreamRegistry;
import org.greatfree.exceptions.DistributedNodeFailedException;

import ca.streaming.news.message.JournalistPostNotification;

// Created: 04/02/2020, Bing Li
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
					System.out.println("JournalistPostNotificationThread: stream received: " + notification.getNews());
					String sKey = Stream.generateKey(notification.getNews().getPublisher(), notification.getNews().getCategory());
					System.out.println("JournalistPostNotificationThread: stream received: publisher = " + notification.getNews().getPublisher());
					System.out.println("JournalistPostNotificationThread: stream received: category = " + notification.getNews().getCategory());
					System.out.println("JournalistPostNotificationThread: stream key = " + sKey);
					if (RootMulticastor.CHILD_STREAM().resetChildren(StreamRegistry.UNICAST().getSubscriberIPs(Stream.generateKey(notification.getNews().getPublisher(), notification.getNews().getCategory()))))
					{
						System.out.println("JournalistPostNotificationThread: stream IS TO BE BROADCAST!!!");
						RootMulticastor.CHILD_STREAM().broadcastNotify(notification);
						System.out.println("JournalistPostNotificationThread: stream IS BROADCAST!!!");
					}
				}
				catch (DistributedNodeFailedException e)
				{
					System.out.println(StreamConfig.ONE_SUBSCRIBER_LEAVE);
				}
				catch (InterruptedException | InstantiationException | IllegalAccessException | IOException e)
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
