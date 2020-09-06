package org.greatfree.dsf.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.MyDistributedQueueStore;
import org.greatfree.dsf.cps.cache.message.front.DequeueSingleMyStoreDataRequest;
import org.greatfree.dsf.cps.cache.message.front.DequeueSingleMyStoreDataResponse;
import org.greatfree.dsf.cps.cache.message.front.DequeueSingleMyStoreDataStream;

// Created: 08/13/2018, Bing Li
public class DequeueSingleMyStoreDataThread extends RequestQueue<DequeueSingleMyStoreDataRequest, DequeueSingleMyStoreDataStream, DequeueSingleMyStoreDataResponse>
{

	public DequeueSingleMyStoreDataThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		DequeueSingleMyStoreDataStream request;
		DequeueSingleMyStoreDataResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new DequeueSingleMyStoreDataResponse(MyDistributedQueueStore.MIDDLESTORE().dequeue(request.getMessage().getQueueKey()));
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
