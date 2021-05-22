package org.greatfree.framework.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.HelloWorldUnicastResponse;

// Created: 08/26/2018, Bing Li
class HelloWorldUnicastResponseThread extends NotificationQueue<HelloWorldUnicastResponse>
{

	public HelloWorldUnicastResponseThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		HelloWorldUnicastResponse notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
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
