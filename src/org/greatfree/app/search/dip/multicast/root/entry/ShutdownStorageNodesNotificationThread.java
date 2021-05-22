package org.greatfree.app.search.dip.multicast.root.entry;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.framework.multicast.message.ShutdownChildrenAdminNotification;
import org.greatfree.framework.multicast.message.ShutdownChildrenBroadcastNotification;
import org.greatfree.framework.multicast.root.RootMulticastor;

// Created: 12/10/2018, Bing Li
class ShutdownStorageNodesNotificationThread extends NotificationQueue<ShutdownChildrenAdminNotification>
{

	public ShutdownStorageNodesNotificationThread(int taskSize)
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
					notification = this.dequeue();
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
