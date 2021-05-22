package org.greatfree.server;

import java.io.IOException;

import org.greatfree.cluster.message.IsRootOnlineRequest;
import org.greatfree.cluster.message.IsRootOnlineResponse;
import org.greatfree.cluster.message.IsRootOnlineStream;
import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

// Created: 10/27/2018, Bing Li
class IsRootOnlineRequestThread extends RequestQueue<IsRootOnlineRequest, IsRootOnlineStream, IsRootOnlineResponse>
{
	public IsRootOnlineRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		IsRootOnlineStream request;
		IPAddress rootIP;
		IsRootOnlineResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				rootIP = PeerRegistry.SYSTEM().getAddress(request.getMessage().getRootID());
				System.out.println("IsRootOnlineRequestThread: root ID = " + request.getMessage().getRootID());
				if (rootIP != UtilConfig.NO_IP_ADDRESS)
				{
					response = new IsRootOnlineResponse(rootIP, true);
				}
				else
				{
					response = new IsRootOnlineResponse(rootIP, false);
				}
				try
				{
					this.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
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
