package org.greatfree.dsf.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.MySortedDistributedCacheStore;
import org.greatfree.dsf.cps.cache.coordinator.MyTimingDistributedCacheStore;
import org.greatfree.dsf.cps.cache.message.front.ContainsKeyOfCachePointingRequest;
import org.greatfree.dsf.cps.cache.message.front.ContainsKeyOfCachePointingResponse;
import org.greatfree.dsf.cps.cache.message.front.ContainsKeyOfCachePointingStream;

// Created: 07/24/2018, Bing Li
public class ContainsKeyOfCachePointingThread extends RequestQueue<ContainsKeyOfCachePointingRequest, ContainsKeyOfCachePointingStream, ContainsKeyOfCachePointingResponse>
{

	public ContainsKeyOfCachePointingThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		ContainsKeyOfCachePointingStream request;
		ContainsKeyOfCachePointingResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				if (!request.getMessage().isTiming())
				{
					response = new ContainsKeyOfCachePointingResponse(MySortedDistributedCacheStore.MIDDLESTORE().containsKey(request.getMessage().getMapKey(), request.getMessage().getResourceKey()));
				}
				else
				{
					response = new ContainsKeyOfCachePointingResponse(MyTimingDistributedCacheStore.MIDDLESTORE().containsKey(request.getMessage().getMapKey(), request.getMessage().getResourceKey()));
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
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

}
