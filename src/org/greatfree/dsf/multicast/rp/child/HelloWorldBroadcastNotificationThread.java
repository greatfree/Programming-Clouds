package org.greatfree.dsf.multicast.rp.child;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.message.HelloWorldBroadcastNotification;

// Created: 10/21/2018, Bing Li
public class HelloWorldBroadcastNotificationThread extends NotificationQueue<HelloWorldBroadcastNotification>
{

	public HelloWorldBroadcastNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		HelloWorldBroadcastNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					// Forward the notification asynchronously. 10/22/2018, Bing Li
					ChildMulticastor.CHILD().asyncNotify(notification);
					// Blocking solutions ......
					System.out.println("HelloWorldBroadcastNotificationThread-BroadcastNotification received: " + notification.getHelloWorld().getHelloWorld());
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
