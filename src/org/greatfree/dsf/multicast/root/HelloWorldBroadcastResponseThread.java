package org.greatfree.dsf.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.message.HelloWorldBroadcastResponse;

// Created: 08/26/2018, Bing Li
class HelloWorldBroadcastResponseThread extends NotificationQueue<HelloWorldBroadcastResponse>
{

	public HelloWorldBroadcastResponseThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		HelloWorldBroadcastResponse notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
//					RootClient.ROOT().getMulticastReader().getRP().saveResponse(notification);
					RootMulticastor.ROOT().getRP().saveResponse(notification);
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
