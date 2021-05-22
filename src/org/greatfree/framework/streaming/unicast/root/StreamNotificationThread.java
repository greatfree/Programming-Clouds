package org.greatfree.framework.streaming.unicast.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.framework.streaming.Stream;
import org.greatfree.framework.streaming.message.StreamNotification;

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
					notification = this.dequeue();
					System.out.println("StreamNotificationThread: stream received: " + notification.getData());
					RootMulticastor.UNI_STREAM().unicastNotify(Stream.generateKey(notification.getData().getPublisher(), notification.getData().getTopic()), notification);
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
