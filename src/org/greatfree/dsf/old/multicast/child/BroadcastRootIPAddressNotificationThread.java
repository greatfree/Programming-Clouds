package org.greatfree.dsf.old.multicast.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.BoundNotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.MulticastConfig;
import org.greatfree.dsf.multicast.message.MessageDisposer;
import org.greatfree.dsf.multicast.message.OldRootIPAddressBroadcastNotification;

/*
 * The thread processes the notification of RootIPAddressBroadcastNotification concurrently. Since it shares the notifications with other threads, i.e., BroadcastRootIPAddressNotificationThread, it must have the mechanism of synchronization. Therefore, it derives the BoundNotificationQueue rather than NotificationQueue. 11/27/2014, Bing Li
 */

// Created: 05/20/2017, Bing Li
class BroadcastRootIPAddressNotificationThread extends BoundNotificationQueue<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>>
{
	/*
	 * Initialize the thread. 11/27/2014, Bing Li
	 * 
	 * In the constructor, the parameters are explained as follows.
	 * 
	 * 		int taskSize: the size of the notification queue.
	 * 
	 * 		String dispatcherKey: the key of the dispatcher which manages the thread. It is the thread key that shares the notification in the binder below.
	 * 
	 * 		ClusterMessageDisposer<RootIPAddressBroadcastNotification> disposer: the disposer that synchronizes the relevant threads that share the notification and disposes the notification after the sharing is ended.
	 * 
	 */
	public BroadcastRootIPAddressNotificationThread(int taskSize, String dispatcherKey, MessageDisposer<OldRootIPAddressBroadcastNotification> binder)
	{
		super(taskSize, dispatcherKey, binder);
	}

	/*
	 * The thread to broadcast notifications asynchronously. 11/27/2014, Bing Li
	 */
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
					
					// Disseminate the notification of HelloWorldNotification among children. 11/27/2014, Bing Li
					ClusterChildSingleton.CLUSTER().broadcastRootIP(notification, MulticastConfig.SUB_BRANCH_COUNT);
					
					// Notify the binder that the notification is consumed by the thread. 11/27/2014, Bing Li
					this.bind(super.getDispatcherKey(), notification);
				}
				catch (InterruptedException | InstantiationException | IllegalAccessException | IOException e)
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
