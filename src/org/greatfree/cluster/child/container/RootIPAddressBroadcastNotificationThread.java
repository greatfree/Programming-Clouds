package org.greatfree.cluster.child.container;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.message.RootIPAddressBroadcastNotification;

// Created: 01/13/2019, Bing Li
class RootIPAddressBroadcastNotificationThread extends NotificationQueue<RootIPAddressBroadcastNotification>
{

	public RootIPAddressBroadcastNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		RootIPAddressBroadcastNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					Child.CONTAINER().asyncNotify(notification);
					Child.CONTAINER().setRootIP(notification.getRootAddress());
					Child.CONTAINER().joinCluster();
					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException e)
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
