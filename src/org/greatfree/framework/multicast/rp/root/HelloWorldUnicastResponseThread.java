package org.greatfree.framework.multicast.rp.root;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.HelloWorldUnicastResponse;

// Created: 10/22/2018, Bing Li
public class HelloWorldUnicastResponseThread extends NotificationQueue<HelloWorldUnicastResponse>
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
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
		}
		
	}

}
