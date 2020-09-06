package org.greatfree.cluster.root.container;

import java.io.IOException;

import org.greatfree.cluster.message.SelectedChildNotification;
import org.greatfree.cluster.message.SuperfluousResourcesNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 09/06/2020, Bing Li
class SuperfluousResourcesNotificationThread extends NotificationQueue<SuperfluousResourcesNotification>
{

	public SuperfluousResourcesNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SuperfluousResourcesNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					ClusterRoot.CONTAINER().unicastNotify(new SelectedChildNotification(notification.getPoolClusterRootKey(), notification.getPoolClusterRootIP()));
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
