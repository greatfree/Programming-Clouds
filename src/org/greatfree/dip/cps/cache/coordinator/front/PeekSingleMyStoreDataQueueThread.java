package org.greatfree.dip.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.MyDistributedQueueStore;
import org.greatfree.dip.cps.cache.message.front.PeekSingleMyStoreDataQueueRequest;
import org.greatfree.dip.cps.cache.message.front.PeekSingleMyStoreDataQueueResponse;
import org.greatfree.dip.cps.cache.message.front.PeekSingleMyStoreDataQueueStream;

// Created: 08/14/2018, Bing Li
public class PeekSingleMyStoreDataQueueThread extends RequestQueue<PeekSingleMyStoreDataQueueRequest, PeekSingleMyStoreDataQueueStream, PeekSingleMyStoreDataQueueResponse>
{

	public PeekSingleMyStoreDataQueueThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PeekSingleMyStoreDataQueueStream request;
		PeekSingleMyStoreDataQueueResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new PeekSingleMyStoreDataQueueResponse(MyDistributedQueueStore.MIDDLESTORE().dequeue(request.getMessage().getQueueKey()));
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
