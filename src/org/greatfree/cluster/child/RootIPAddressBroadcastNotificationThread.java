package org.greatfree.cluster.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.RootIPAddressBroadcastNotification;

// Created: 09/23/2018, Bing Li
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
					notification = this.dequeue();
					Child.CLUSTER().asyncNotify(notification);
					Child.CLUSTER().setRootIP(notification.getRootAddress());
					Child.CLUSTER().joinCluster();
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
