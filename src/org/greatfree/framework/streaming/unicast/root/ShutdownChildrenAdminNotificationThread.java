package org.greatfree.framework.streaming.unicast.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.framework.multicast.message.ShutdownChildrenAdminNotification;
import org.greatfree.framework.multicast.message.ShutdownChildrenBroadcastNotification;

// Created: 03/22/2020, Bing Li
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
					RootMulticastor.UNI_STREAM().broadcastNotify(new ShutdownChildrenBroadcastNotification());
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
