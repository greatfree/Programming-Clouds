package org.greatfree.framework.old.multicast.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.multicast.message.ShutdownChildrenAdminNotification;

// Created: 05/19/2017, Bing Li
class ShutdownChildrenAdminNotificationThread extends NotificationQueue<ShutdownChildrenAdminNotification>
{

	public ShutdownChildrenAdminNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ShutdownChildrenAdminNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();

					// Broadcast shutdown notification to all of its children. 05/20/2017, Bing Li
					ClusterRootSingleton.CLUSTER().broadcastShutdownNotify(MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT);
					
					this.disposeMessage(notification);
				}
				catch (InterruptedException | InstantiationException | IllegalAccessException | IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 01/20/2016, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

}
