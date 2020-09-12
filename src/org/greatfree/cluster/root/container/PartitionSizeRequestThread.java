package org.greatfree.cluster.root.container;

import java.io.IOException;

import org.greatfree.cluster.message.PartitionSizeRequest;
import org.greatfree.cluster.message.PartitionSizeResponse;
import org.greatfree.cluster.message.PartitionSizeStream;
import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;

// Created: 09/09/2020, Bing Li
class PartitionSizeRequestThread extends RequestQueue<PartitionSizeRequest, PartitionSizeStream, PartitionSizeResponse>
{

	public PartitionSizeRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PartitionSizeStream request;
		PartitionSizeResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new PartitionSizeResponse(ClusterRoot.CONTAINER().getPartitionSize());
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
