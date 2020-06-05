package ca.streaming.news.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.streaming.Stream;
import org.greatfree.exceptions.DistributedNodeFailedException;

import ca.streaming.news.message.JournalistPostNotification;

// Created: 03/31/2020, Bing Li
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
					NewsRootMulticastor.STREAM().unicastNotify(Stream.generateKey(notification.getNews().getPublisher(), notification.getNews().getCategory()), notification);
					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException | DistributedNodeFailedException e)
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
