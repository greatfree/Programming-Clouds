package org.greatfree.framework.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedReadStackStore;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedStackStore;
import org.greatfree.framework.cps.cache.message.front.PeekSingleMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.front.PeekSingleMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.front.PeekSingleMyStoreDataStream;

// Created: 08/09/2018, Bing Li
public class PeekSingleMyStoreDataThread extends RequestQueue<PeekSingleMyStoreDataRequest, PeekSingleMyStoreDataStream, PeekSingleMyStoreDataResponse>
{

	public PeekSingleMyStoreDataThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PeekSingleMyStoreDataStream request;
		PeekSingleMyStoreDataResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				if (!request.getMessage().isReading())
				{
					response = new PeekSingleMyStoreDataResponse(MyDistributedStackStore.MIDDLESTORE().peek(request.getMessage().getStackKey()));
				}
				else
				{
					response = new PeekSingleMyStoreDataResponse(MyDistributedReadStackStore.MIDDLESTORE().peek(request.getMessage().getStackKey()));
				}
				try
				{
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (IOException e)
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
