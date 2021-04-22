package org.greatfree.framework.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastNotification;

// Created: 09/10/2018, Bing Li
class HelloWorldBroadcastNotificationThread extends NotificationQueue<HelloWorldBroadcastNotification>
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
					ChildMulticastor.CHILD().asyncNotify(notification);
//					ChildMulticastor.CHILD().notify(notification);

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
