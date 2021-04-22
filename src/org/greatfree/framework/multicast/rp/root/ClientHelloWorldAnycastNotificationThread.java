package org.greatfree.framework.multicast.rp.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.framework.multicast.message.HelloWorldAnycastNotification;

// Created: 10/21/2018, Bing Li
public class ClientHelloWorldAnycastNotificationThread extends NotificationQueue<HelloWorldAnycastNotification>
{

	public ClientHelloWorldAnycastNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		HelloWorldAnycastNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					RootMulticastor.ROOT().anycastNotify(notification);
					this.disposeMessage(notification);
				}
				catch (InterruptedException | InstantiationException | IllegalAccessException | IOException | DistributedNodeFailedException e)
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
