package org.greatfree.framework.old.multicast.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.BoundNotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastNotification;
import org.greatfree.message.MessageDisposer;

/*
 * The thread processes the notification of HelloWorldNotification concurrently. Since it shares the notifications with other threads, i.e., HelloWorldNotificationThread, it must have the mechanism of synchronization. Therefore, it derives the BoundNotificationQueue rather than NotificationQueue. 11/27/2014, Bing Li
 */

// Created: 05/16/2017, Bing Li
class BroadcastHelloWorldNotificationThread extends BoundNotificationQueue<OldHelloWorldBroadcastNotification, MessageDisposer<OldHelloWorldBroadcastNotification>>
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
	 * 		ClusterMessageDisposer<HelloWorldBroadcastNotification> disposer: the disposer that synchronizes the relevant threads that share the notification and disposes the notification after the sharing is ended.
	 * 
	 */
	public BroadcastHelloWorldNotificationThread(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastNotification> binder)
	{
		super(taskSize, dispatcherKey, binder);
	}

	/*
	 * The thread to broadcast notifications asynchronously. 11/27/2014, Bing Li
	 */
	@Override
	public void run()
	{
		// Declare a notification instance of HelloWorldNotification. 11/27/2014, Bing Li
		OldHelloWorldBroadcastNotification notification;
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
					
					try
					{
						// Disseminate the notification of HelloWorldNotification among children. 11/27/2014, Bing Li
						ClusterChildSingleton.CLUSTER().broadcastNotify(notification, MulticastConfig.SUB_BRANCH_COUNT);
					}
					catch (InstantiationException | IllegalAccessException | IOException e)
					{
						e.printStackTrace();
					}
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
