package org.greatfree.cluster.root.container;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.container.Notification;

// Created: 01/14/2019, Bing Li
class RootNotificationThread extends NotificationQueue<Notification>
{

	public RootNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		Notification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					ClusterRoot.CONTAINER().processNotification(notification);
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
