package org.greatfree.framework.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MySortedDistributedReadCacheStore;
import org.greatfree.framework.cps.cache.message.front.IsCacheReadExistedInPointingStoreRequest;
import org.greatfree.framework.cps.cache.message.front.IsCacheReadExistedInPointingStoreResponse;
import org.greatfree.framework.cps.cache.message.front.IsCacheReadExistedInPointingStoreStream;

// Created: 08/05/2018, Bing Li
public class IsCacheReadExistedInPointingStoreThread extends RequestQueue<IsCacheReadExistedInPointingStoreRequest, IsCacheReadExistedInPointingStoreStream, IsCacheReadExistedInPointingStoreResponse>
{

	public IsCacheReadExistedInPointingStoreThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		IsCacheReadExistedInPointingStoreStream request;
		IsCacheReadExistedInPointingStoreResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				response = new IsCacheReadExistedInPointingStoreResponse(MySortedDistributedReadCacheStore.MIDDLESTORE().isCacheExisted(request.getMessage().getMapKey()));
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
