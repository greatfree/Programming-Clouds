package org.greatfree.app.search.dip.multicast.root.entry;

import java.io.IOException;

import org.greatfree.app.search.dip.multicast.message.LocationNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.framework.multicast.root.RootMulticastor;

// Created: 10/07/2018, Bing Li
class LocationNotificationThread extends NotificationQueue<LocationNotification>
{

	public LocationNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		LocationNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					RootMulticastor.ROOT().broadcastNotify(notification);
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
