package org.greatfree.cluster.root.container;

import java.io.IOException;

import org.greatfree.cluster.message.ClusterSizeRequest;
import org.greatfree.cluster.message.ClusterSizeResponse;
import org.greatfree.cluster.message.ClusterSizeStream;
import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;

// Created: 09/12/2020, Bing Li
class ClusterSizeRequestThread extends RequestQueue<ClusterSizeRequest, ClusterSizeStream, ClusterSizeResponse>
{

	public ClusterSizeRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ClusterSizeStream request;
		ClusterSizeResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new ClusterSizeResponse(ClusterRoot.CONTAINER().getChildrenCount());
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
