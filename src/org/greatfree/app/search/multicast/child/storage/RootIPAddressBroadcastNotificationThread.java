package org.greatfree.app.search.multicast.child.storage;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.child.ChildMulticastor;
import org.greatfree.message.multicast.container.RootAddressNotification;

// Created: 09/28/2018, Bing Li
class RootIPAddressBroadcastNotificationThread extends NotificationQueue<RootAddressNotification>
{

	public RootIPAddressBroadcastNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		RootAddressNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					ChildMulticastor.CHILD().asyncNotify(notification);
					StorageNode.STORAGE().setRootIP(notification.getRootAddress());
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 11/26/2014, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
