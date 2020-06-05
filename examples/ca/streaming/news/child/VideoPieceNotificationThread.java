package ca.streaming.news.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.streaming.Stream;
import org.greatfree.dip.streaming.StreamConfig;
import org.greatfree.dip.streaming.broadcast.child.RootMulticastor;
import org.greatfree.dip.streaming.unicast.child.StreamRegistry;
import org.greatfree.exceptions.DistributedNodeFailedException;

import ca.streaming.news.message.VideoPieceNotification;

// Created: 04/02/2020, Bing Li
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
					if (RootMulticastor.CHILD_STREAM().resetChildren(StreamRegistry.UNICAST().getSubscriberIPs(Stream.generateKey(notification.getPost().getPublisher(), notification.getPost().getCategory()))))
					{
						RootMulticastor.CHILD_STREAM().broadcastNotify(notification);
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
