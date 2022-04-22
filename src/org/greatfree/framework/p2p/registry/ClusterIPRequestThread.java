package org.greatfree.framework.p2p.registry;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.multicast.PrimitiveClusterIPRequest;
import org.greatfree.message.multicast.ClusterIPResponse;
import org.greatfree.message.multicast.PrimitiveClusterIPStream;

/*
 * The thread retrieves all of the registered IPs of the distributed nodes within one cluster. 05/08/2017, Bing Li
 */

// Created: 05/08/2017, Bing Li
class ClusterIPRequestThread extends RequestQueue<PrimitiveClusterIPRequest, PrimitiveClusterIPStream, ClusterIPResponse>
{

	public ClusterIPRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PrimitiveClusterIPStream request;
		ClusterIPResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				if (request.getMessage().getNodes() == null)
				{
					// Updated in XTU. 05/17/2017, Bing Li
					// Generate the response that encloses all of IPs of the nodes in the cluster. 05/08/2017, Bing Li
					response = new ClusterIPResponse(AccountRegistry.APPLICATION().getIPPorts(PeerRegistry.SYSTEM().getIPPorts()));
				}
				else
				{
					response = new ClusterIPResponse(AccountRegistry.APPLICATION().getIPPorts(PeerRegistry.SYSTEM().getIPPorts(request.getMessage().getNodes())));
				}
				
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
				// Wait for some time when the queue is empty. During the period and before the thread is killed, some new requests might be received. If so, the thread can keep working. 02/15/2016, Bing Li
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

}
