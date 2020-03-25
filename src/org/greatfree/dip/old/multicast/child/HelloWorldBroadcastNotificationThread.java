package org.greatfree.dip.old.multicast.child;

import org.greatfree.concurrency.reactive.BoundNotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.message.MessageDisposer;
import org.greatfree.dip.multicast.message.OldHelloWorldBroadcastNotification;

/*
 * The thread processes the instances of HelloWorldNotification from the root. Since the notification is shared by the one that needs to forward it, both of them are derived from BoundNotificationQueue for synchronization. 11/26/2014, Bing Li
 */

// Created: 05/15/2017, Bing Li
class HelloWorldBroadcastNotificationThread extends BoundNotificationQueue<OldHelloWorldBroadcastNotification, MessageDisposer<OldHelloWorldBroadcastNotification>>
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
	 * 		ClusterMessageDisposer<HelloWorldBroadcastNotification> binder: the binder that synchronizes the relevant threads that share the notification and disposes the notification after the sharing is ended.
	 * 
	 */
	public HelloWorldBroadcastNotificationThread(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastNotification> binder)
	{
		super(binder, dispatcherKey);
	}

	/*
	 * The thread to process notifications asynchronously. 11/26/2014, Bing Li
	 */
	@Override
	public void run()
	{
		// Declare a notification instance of HelloWorldNotification. 11/26/2014, Bing Li
		OldHelloWorldBroadcastNotification notification;
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
					// Just present the received message. 06/16/2017, Bing Li
					System.out.println("HelloWorldBroadcastNotificationThread-BroadcastNotification received: " + notification.getHelloWorld().getHelloWorld());
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
