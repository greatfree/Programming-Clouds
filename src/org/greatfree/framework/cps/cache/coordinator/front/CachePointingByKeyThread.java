package org.greatfree.framework.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MySortedDistributedCacheStore;
import org.greatfree.framework.cps.cache.coordinator.MyTimingDistributedCacheStore;
import org.greatfree.framework.cps.cache.message.front.CachePointingByKeyRequest;
import org.greatfree.framework.cps.cache.message.front.CachePointingByKeyResponse;
import org.greatfree.framework.cps.cache.message.front.CachePointingByKeyStream;

// Created: 07/24/2018, Bing Li
public class CachePointingByKeyThread extends RequestQueue<CachePointingByKeyRequest, CachePointingByKeyStream, CachePointingByKeyResponse>
{

	public CachePointingByKeyThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		CachePointingByKeyStream request;
		CachePointingByKeyResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				if (!request.getMessage().isTiming())
				{
					response = new CachePointingByKeyResponse(MySortedDistributedCacheStore.MIDDLESTORE().get(request.getMessage().getMapKey(), request.getMessage().getResourceKey()));
				}
				else
				{
					response = new CachePointingByKeyResponse(MyTimingDistributedCacheStore.MIDDLESTORE().get(request.getMessage().getMapKey(), request.getMessage().getResourceKey()));
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
