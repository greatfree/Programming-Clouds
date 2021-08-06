package org.greatfree.cluster.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.container.ClusterNotification;

// Created: 09/23/2018, Bing Li
class RootNotificationThread extends NotificationQueue<ClusterNotification>
{

	public RootNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ClusterNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					ClusterRoot.CLUSTER().processNotification(notification);
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
