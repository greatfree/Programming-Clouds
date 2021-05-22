package org.greatfree.cluster.root.container;

import java.io.IOException;

import org.greatfree.cluster.message.AdditionalChildrenRequest;
import org.greatfree.cluster.message.AdditionalChildrenResponse;
import org.greatfree.cluster.message.AdditionalChildrenStream;
import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;

// Created: 09/12/2020, Bing Li
class AdditionalChildrenRequestThread extends RequestQueue<AdditionalChildrenRequest, AdditionalChildrenStream, AdditionalChildrenResponse>
{

	public AdditionalChildrenRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		AdditionalChildrenStream request;
		AdditionalChildrenResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				response = new AdditionalChildrenResponse(ClusterRoot.CONTAINER().getChildrenKeys(request.getMessage().getSize()));
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
