package org.greatfree.cluster.child;

import java.io.IOException;

import org.greatfree.cluster.child.container.ChildServiceProvider;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.Request;

// Created: 09/23/2018, Bing Li
class ChildRequestThread extends NotificationQueue<Request>
{

	public ChildRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		Request request;
		MulticastResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					request = this.dequeue();
					Child.CLUSTER().forward(request);
					response = ChildServiceProvider.CHILD().processRequest(request);
					Child.CLUSTER().notifyRoot(response);
					this.disposeMessage(request);
					this.dispose(response);
				}
				catch (InterruptedException | IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
