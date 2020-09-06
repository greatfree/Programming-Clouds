package org.greatfree.dsf.streaming.broadcast.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.streaming.message.StreamNotification;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 03/18/2020, Bing Li
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
					String selectedChildKey = RootMulticastor.BROADCAST_STREAM().getRandomChildKey();
					System.out.println("selectedChildKey = " + selectedChildKey);
					notification.setChildKey(selectedChildKey);
					RootMulticastor.BROADCAST_STREAM().broadcastNotify(notification);
					System.out.println("StreamNotificationThread: stream received: " + notification.getData());
					this.disposeMessage(notification);
				}
				catch (InterruptedException | InstantiationException | IllegalAccessException | IOException | DistributedNodeFailedException e)
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
