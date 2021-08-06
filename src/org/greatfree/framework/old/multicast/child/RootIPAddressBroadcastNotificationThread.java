package org.greatfree.framework.old.multicast.child;

import org.greatfree.concurrency.reactive.BoundNotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.OldRootIPAddressBroadcastNotification;
import org.greatfree.message.MessageDisposer;

/*
 * The thread processes the instances of RootIPAddressBroadcastNotification from the root. Since the notification is shared by the one that needs to forward it, both of them are derived from BoundNotificationQueue for synchronization. 11/26/2014, Bing Li
 */

// Created: 05/20/2017, Bing Li
class RootIPAddressBroadcastNotificationThread extends BoundNotificationQueue<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>>
{
	/*
	 * Initialize the thread. 11/26/2014, Bing Li
	 * 
	 * In the constructor, the parameters are explained as follows.
	 * 
	 * 		int taskSize: the size of the notification queue.
	 * 
	 * 		String dispatcherKey: the key of the dispatcher which manages the thread. It is the thread key that shares the notification in the binder below.
	 * 
	 * 		ClusterMessageDisposer<RootIPAddressBroadcastNotification> binder: the binder that synchronizes the relevant threads that share the notification and disposes the notification after the sharing is ended.
	 * 
	 */
	public RootIPAddressBroadcastNotificationThread(int taskSize, String dispatcherKey, MessageDisposer<OldRootIPAddressBroadcastNotification> binder)
	{
		super(taskSize, dispatcherKey, binder);
	}

	/*
	 * The thread to process notifications asynchronously. 11/26/2014, Bing Li
	 */
	@Override
	public void run()
	{
		// Declare a notification instance of RootIPAddressBroadcastNotification. 11/26/2014, Bing Li
		OldRootIPAddressBroadcastNotification notification;
		// The thread always runs until it is shutdown by the BoundNotificationDispatcher. 11/26/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 11/26/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 11/26/2014, Bing Li
					notification = this.getNotification();
					
					// Keep the root IP address. 05/20/2017, Bing Li
					ClusterChildSingleton.CLUSTER().setRootIP(notification.getRootAddress());
//					ClusterConfig.CLUSTER_ROOT_ADDRESS = notification.getRootAddress();
					
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
