package ca.streaming.news.subscriber;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.child.ChildMulticastor;
import org.greatfree.dip.streaming.StreamConfig;
import org.greatfree.dip.streaming.subscriber.SubscriberDB;
import org.greatfree.util.FileManager;

import ca.streaming.news.message.VideoPieceNotification;
import ca.streaming.news.publisher.NewsConfig;

// Created: 04/03/2020, Bing Li
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
					ChildMulticastor.CHILD().asyncNotify(notification);
					FileManager.saveFile(NewsConfig.FILE_DES_PATH, notification.getVideoPiece(), true);
					SubscriberDB.DB().save(notification.getPost().getMessage());
					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException e)
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
