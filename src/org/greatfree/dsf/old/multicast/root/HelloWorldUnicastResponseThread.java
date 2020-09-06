package org.greatfree.dsf.old.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.message.HelloWorldUnicastResponse;

/*
 * The thread receives the remote responses that answer the unicast request concurrently. 05/21/2017, Bing Li
 */

// Created: 05/21/2017, Bing Li
class HelloWorldUnicastResponseThread extends NotificationQueue<HelloWorldUnicastResponse>
{

	public HelloWorldUnicastResponseThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		HelloWorldUnicastResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					response = this.getNotification();
					// Collect responses from the children. 05/21/2017, Bing Li
					ClusterRootSingleton.CLUSTER().unicastResponseReceived(response);
					
					this.disposeMessage(response);
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
