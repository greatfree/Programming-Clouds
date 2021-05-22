package org.greatfree.framework.multicast.rp.child;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.HelloWorldUnicastNotification;

// Created: 10/22/2018, Bing Li
public class HelloWorldUnicastNotificationThread extends NotificationQueue<HelloWorldUnicastNotification>
{

	public HelloWorldUnicastNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		HelloWorldUnicastNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();

					// Display the message on the screen. 06/17/2017, Bing Li
					System.out.println("HelloWorldUnicastNotificationThread-notification: " + notification.getHelloWorld().getHelloWorld());

					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 01/20/2016, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
