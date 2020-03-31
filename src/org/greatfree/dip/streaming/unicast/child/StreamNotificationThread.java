package org.greatfree.dip.streaming.unicast.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.streaming.Stream;
import org.greatfree.dip.streaming.StreamConfig;
import org.greatfree.dip.streaming.broadcast.child.RootMulticastor;
import org.greatfree.dip.streaming.message.StreamNotification;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 03/22/2020, Bing Li
class StreamNotificationThread extends NotificationQueue<StreamNotification>
{

	public StreamNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		StreamNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					System.out.println("StreamNotificationThread: stream received: " + notification.getData());
					if (RootMulticastor.CHILD_STREAM().resetChildren(StreamRegistry.UNICAST().getSubscriberIPs(Stream.generateKey(notification.getData().getPublisher(), notification.getData().getTopic()))))
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
