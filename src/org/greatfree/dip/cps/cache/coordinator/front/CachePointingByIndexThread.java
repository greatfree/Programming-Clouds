package org.greatfree.dip.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.MySortedDistributedCacheStore;
import org.greatfree.dip.cps.cache.coordinator.MyTimingDistributedCacheStore;
import org.greatfree.dip.cps.cache.message.front.CachePointingByIndexRequest;
import org.greatfree.dip.cps.cache.message.front.CachePointingByIndexResponse;
import org.greatfree.dip.cps.cache.message.front.CachePointingByIndexStream;

// Created: 07/24/2018, Bing Li
public class CachePointingByIndexThread extends RequestQueue<CachePointingByIndexRequest, CachePointingByIndexStream, CachePointingByIndexResponse>
{

	public CachePointingByIndexThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		CachePointingByIndexStream request;
		CachePointingByIndexResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				if (!request.getMessage().isTiming())
				{
					response = new CachePointingByIndexResponse(MySortedDistributedCacheStore.MIDDLESTORE().get(request.getMessage().getMapKey(), request.getMessage().getIndex()));
				}
				else
				{
					response = new CachePointingByIndexResponse(MyTimingDistributedCacheStore.MIDDLESTORE().get(request.getMessage().getMapKey(), request.getMessage().getIndex()));
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
