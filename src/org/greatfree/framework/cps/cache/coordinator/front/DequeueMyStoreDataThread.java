package org.greatfree.framework.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedQueueStore;
import org.greatfree.framework.cps.cache.message.prefetch.DequeueMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.prefetch.DequeueMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.prefetch.DequeueMyStoreDataStream;

// Created: 08/14/2018, Bing Li
public class DequeueMyStoreDataThread extends RequestQueue<DequeueMyStoreDataRequest, DequeueMyStoreDataStream, DequeueMyStoreDataResponse>
{

	public DequeueMyStoreDataThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		DequeueMyStoreDataStream request;
		DequeueMyStoreDataResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				try
				{
					response = new DequeueMyStoreDataResponse(MyDistributedQueueStore.MIDDLESTORE().dequeueAll(request.getMessage().getQueueKey(), request.getMessage().getCount()));
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (DistributedListFetchException | IOException e)
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
