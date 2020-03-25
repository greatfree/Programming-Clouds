package org.greatfree.dip.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.MySortedDistributedCacheStore;
import org.greatfree.dip.cps.cache.coordinator.MyTimingDistributedCacheStore;
import org.greatfree.dip.cps.cache.message.front.IsCacheExistedInPointingStoreRequest;
import org.greatfree.dip.cps.cache.message.front.IsCacheExistedInPointingStoreResponse;
import org.greatfree.dip.cps.cache.message.front.IsCacheExistedInPointingStoreStream;

// Created: 07/24/2018, Bing Li
public class IsCacheExistedInPointingStoreThread extends RequestQueue<IsCacheExistedInPointingStoreRequest, IsCacheExistedInPointingStoreStream, IsCacheExistedInPointingStoreResponse>
{

	public IsCacheExistedInPointingStoreThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		IsCacheExistedInPointingStoreStream request;
		IsCacheExistedInPointingStoreResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				if (!request.getMessage().isTiming())
				{
					response = new IsCacheExistedInPointingStoreResponse(MySortedDistributedCacheStore.MIDDLESTORE().isCacheExisted(request.getMessage().getMapKey()));
				}
				else
				{
					response = new IsCacheExistedInPointingStoreResponse(MyTimingDistributedCacheStore.MIDDLESTORE().isCacheExisted(request.getMessage().getMapKey()));
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
