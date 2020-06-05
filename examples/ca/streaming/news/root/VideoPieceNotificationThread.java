package ca.streaming.news.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.streaming.Stream;
import org.greatfree.exceptions.DistributedNodeFailedException;

import ca.streaming.news.message.VideoPieceNotification;

// Created: 04/01/2020, Bing Li
class VideoPieceNotificationThread extends NotificationQueue<VideoPieceNotification>
{

	public VideoPieceNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		VideoPieceNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					System.out.println("VideoPieceNotificationThread: stream received: " + notification.getPost());
					NewsRootMulticastor.STREAM().unicastNotify(Stream.generateKey(notification.getPost().getPublisher(), notification.getPost().getCategory()), notification);
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
