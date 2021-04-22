package org.greatfree.framework.multicast.bound.child;

import org.greatfree.concurrency.reactive.BoundNotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.MessageDisposer;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastNotification;

// Created: 08/26/2018, Bing Li
public class HelloWorldBroadcastNotificationThread extends BoundNotificationQueue<OldHelloWorldBroadcastNotification, MessageDisposer<OldHelloWorldBroadcastNotification>>
{

	public HelloWorldBroadcastNotificationThread(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastNotification> binder)
	{
		super(taskSize, dispatcherKey, binder);
	}

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
