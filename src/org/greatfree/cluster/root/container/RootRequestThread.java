package org.greatfree.cluster.root.container;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.ClusterRequestStream;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

// Created: 01/13/2019, Bing Li
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
					response = ClusterRoot.CONTAINER().processRequest(request.getMessage());
					
//					System.out.println("RootRequestThread-run(): " + request.getMessage().getCollaboratorKey() + "'s response is obtained and to be sent back to the client ...");
					
					this.respond(request.getOutStream(), request.getLock(), response);
//					System.out.println("RootRequestThread-run(): " + request.getMessage().getCollaboratorKey() + "'s response is done to send back to the client ...");
					this.disposeMessage(request, response);
				}
				catch (IOException | DistributedNodeFailedException | ClassNotFoundException | RemoteReadException e)
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
