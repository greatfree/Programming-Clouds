package org.greatfree.dsf.streaming.unicast.child;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.child.ChildMulticastor;
import org.greatfree.dsf.multicast.message.RootIPAddressBroadcastNotification;

// Created: 03/22/2020, Bing Li
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
					ChildMulticastor.CHILD().asyncNotify(notification);
					ChildPeer.UNICAST().setRootIP(notification.getRootAddress());
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
