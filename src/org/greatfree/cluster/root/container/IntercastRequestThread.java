package org.greatfree.cluster.root.container;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.IntercastRequestStream;
import org.greatfree.message.multicast.container.Response;

// Created: 03/04/2019, Bing Li
class IntercastRequestThread extends RequestQueue<IntercastRequest, IntercastRequestStream, Response>
{

	public IntercastRequestThread(int notificationQueueSize)
	{
		super(notificationQueueSize);
	}

	@Override
	public void run()
	{
		IntercastRequestStream request;
		Response response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				try
				{
					response = ClusterRoot.CONTAINER().processIntercastRequest(request.getMessage());
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (ClassNotFoundException | RemoteReadException | IOException e)
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

