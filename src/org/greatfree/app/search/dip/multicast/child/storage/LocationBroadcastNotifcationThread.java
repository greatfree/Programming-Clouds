package org.greatfree.app.search.dip.multicast.child.storage;

import org.greatfree.app.search.dip.multicast.message.LocationNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.child.ChildMulticastor;

// Created: 10/07/2018, Bing Li
class LocationBroadcastNotifcationThread extends NotificationQueue<LocationNotification>
{

	public LocationBroadcastNotifcationThread(int taskSize)
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
					ChildMulticastor.CHILD().asyncNotify(notification);
					UserPreferences.STORAGE().setLocale(notification.getUserKey(), notification.isInternational());
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
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
