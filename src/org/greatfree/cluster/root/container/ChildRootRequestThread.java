package org.greatfree.cluster.root.container;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ChildRootStream;

// Created: 09/14/2020, Bing Li
class ChildRootRequestThread extends RequestQueue<ChildRootRequest, ChildRootStream, ChildRootResponse>
{

	public ChildRootRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ChildRootStream request;
		ChildRootResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				response = ClusterRoot.CONTAINER().processRequest(request.getMessage());
				try
				{
					this.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				this.disposeMessage(request, response);
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
