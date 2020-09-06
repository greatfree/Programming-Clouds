package org.greatfree.dsf.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.MySortedDistributedCacheStore;
import org.greatfree.dsf.cps.cache.coordinator.MyTimingDistributedCacheStore;
import org.greatfree.dsf.cps.cache.message.front.IsCacheEmptyInPointingStoreRequest;
import org.greatfree.dsf.cps.cache.message.front.IsCacheEmptyInPointingStoreResponse;
import org.greatfree.dsf.cps.cache.message.front.IsCacheEmptyInPointingStoreStream;

// Created: 07/24/2018, Bing Li
public class IsCacheEmptyInPointingStoreThread extends RequestQueue<IsCacheEmptyInPointingStoreRequest, IsCacheEmptyInPointingStoreStream, IsCacheEmptyInPointingStoreResponse>
{

	public IsCacheEmptyInPointingStoreThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		IsCacheEmptyInPointingStoreStream request;
		IsCacheEmptyInPointingStoreResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				if (!request.getMessage().isTiming())
				{
					response = new IsCacheEmptyInPointingStoreResponse(MySortedDistributedCacheStore.MIDDLESTORE().isCacheEmpty(request.getMessage().getMapKey()));
				}
				else
				{
					response = new IsCacheEmptyInPointingStoreResponse(MyTimingDistributedCacheStore.MIDDLESTORE().isCacheEmpty(request.getMessage().getMapKey()));
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
