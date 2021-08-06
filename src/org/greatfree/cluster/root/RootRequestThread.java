package org.greatfree.cluster.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.ClusterRequestStream;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

// Created: 09/23/2018, Bing Li
class RootRequestThread extends RequestQueue<ClusterRequest, ClusterRequestStream, CollectedClusterResponse>
{

	public RootRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		ClusterRequestStream request;
		CollectedClusterResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				try
				{
					response = ClusterRoot.CLUSTER().processRequest(request.getMessage());
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (IOException | DistributedNodeFailedException e)
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
