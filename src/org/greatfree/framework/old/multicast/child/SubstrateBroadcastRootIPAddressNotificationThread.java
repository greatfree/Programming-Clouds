package org.greatfree.framework.old.multicast.child;

import org.greatfree.concurrency.reactive.BoundNotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.OldRootIPAddressBroadcastNotification;
import org.greatfree.message.MessageDisposer;

// Created: 07/19/2017, Bing Li
class SubstrateBroadcastRootIPAddressNotificationThread extends BoundNotificationQueue<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>>
{

	public SubstrateBroadcastRootIPAddressNotificationThread(int taskSize, String dispatcherKey, MessageDisposer<OldRootIPAddressBroadcastNotification> binder)
	{
		super(taskSize, dispatcherKey, binder);
	}

	@Override
	public void run()
	{
		// Declare a notification instance of RootIPAddressBroadcastNotification. 11/27/2014, Bing Li
		OldRootIPAddressBroadcastNotification notification;
		// The thread always runs until it is shutdown by the BoundNotificationDispatcher. 11/27/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/27/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 11/27/2014, Bing Li
					notification = this.getNotification();
					
					/*
					 * The class is abandoned. 12/15/2018, Bing Li
					 */
					// Disseminate the notification of HelloWorldNotification among children. 11/27/2014, Bing Li
//					ClusterChildSubstrate.CLUSTER().broadcastRootIP(notification, MulticastConfig.SUB_BRANCH_COUNT);
					
					// Notify the binder that the notification is consumed by the thread. 11/27/2014, Bing Li
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
