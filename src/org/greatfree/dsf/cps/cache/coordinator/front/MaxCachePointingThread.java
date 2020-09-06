package org.greatfree.dsf.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.MySortedDistributedCacheStore;
import org.greatfree.dsf.cps.cache.coordinator.MyTimingDistributedCacheStore;
import org.greatfree.dsf.cps.cache.message.front.MaxCachePointingRequest;
import org.greatfree.dsf.cps.cache.message.front.MaxCachePointingResponse;
import org.greatfree.dsf.cps.cache.message.front.MaxCachePointingStream;

// Created: 07/24/2018, Bing Li
public class MaxCachePointingThread extends RequestQueue<MaxCachePointingRequest, MaxCachePointingStream, MaxCachePointingResponse>
{

	public MaxCachePointingThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		MaxCachePointingStream request;
		MaxCachePointingResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				if (!request.getMessage().isTiming())
				{
					response = new MaxCachePointingResponse(MySortedDistributedCacheStore.MIDDLESTORE().getMax(request.getMessage().getMapKey()));
				}
				else
				{
					response = new MaxCachePointingResponse(MyTimingDistributedCacheStore.MIDDLESTORE().getMax(request.getMessage().getMapKey()));
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
