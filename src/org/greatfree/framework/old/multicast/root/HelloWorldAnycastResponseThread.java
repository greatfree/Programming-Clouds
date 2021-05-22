package org.greatfree.framework.old.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.HelloWorldAnycastResponse;

/*
 * The thread receives the remote responses that answer the anycast request concurrently. 05/21/2017, Bing Li
 */

// Created: 05/21/2017, Bing Li
class HelloWorldAnycastResponseThread extends NotificationQueue<HelloWorldAnycastResponse>
{

	public HelloWorldAnycastResponseThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		HelloWorldAnycastResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					response = this.dequeue();
					
					// Collect responses from the children. 05/21/2017, Bing Li
					ClusterRootSingleton.CLUSTER().anycastResponseReceived(response);
					
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
