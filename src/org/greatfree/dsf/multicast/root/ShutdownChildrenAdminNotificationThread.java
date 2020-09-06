package org.greatfree.dsf.multicast.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.message.ShutdownChildrenAdminNotification;
import org.greatfree.dsf.multicast.message.ShutdownChildrenBroadcastNotification;
import org.greatfree.exceptions.DistributedNodeFailedException;

// Created: 08/26/2018, Bing Li
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
					RootMulticastor.ROOT().broadcastNotify(new ShutdownChildrenBroadcastNotification());
					this.disposeMessage(notification);
				}
				catch (InterruptedException | InstantiationException | IllegalAccessException | IOException | DistributedNodeFailedException e)
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
