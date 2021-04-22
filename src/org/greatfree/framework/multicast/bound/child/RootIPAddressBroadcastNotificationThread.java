package org.greatfree.framework.multicast.bound.child;

import org.greatfree.concurrency.reactive.BoundNotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.MessageDisposer;
import org.greatfree.framework.multicast.message.OldRootIPAddressBroadcastNotification;

// Created: 08/26/2018, Bing Li
public class RootIPAddressBroadcastNotificationThread extends BoundNotificationQueue<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>>
{

	public RootIPAddressBroadcastNotificationThread(int taskSize, String dispatcherKey, MessageDisposer<OldRootIPAddressBroadcastNotification> binder)
	{
		super(taskSize, dispatcherKey, binder);
	}

	@Override
	public void run()
	{
		OldRootIPAddressBroadcastNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					ChildPeer.CHILD().setRootIP(notification.getRootAddress());
					// Notify the binder that the notification is consumed by the thread. 11/26/2014, Bing Li
					this.bind(super.getDispatcherKey(), notification);
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
