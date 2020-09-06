package org.greatfree.dsf.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.MyDistributedReadStackStore;
import org.greatfree.dsf.cps.cache.coordinator.MyDistributedStackStore;
import org.greatfree.dsf.cps.cache.message.front.PopSingleMyStoreDataRequest;
import org.greatfree.dsf.cps.cache.message.front.PopSingleMyStoreDataResponse;
import org.greatfree.dsf.cps.cache.message.front.PopSingleMyStoreDataStream;

// Created: 08/09/2018, Bing Li
public class PopSingleMyStoreDataThread extends RequestQueue<PopSingleMyStoreDataRequest, PopSingleMyStoreDataStream, PopSingleMyStoreDataResponse>
{

	public PopSingleMyStoreDataThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PopSingleMyStoreDataStream request;
		PopSingleMyStoreDataResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				if (!request.getMessage().isReading())
				{
					response = new PopSingleMyStoreDataResponse(MyDistributedStackStore.MIDDLESTORE().pop(request.getMessage().getStackKey()));
				}
				else
				{
					response = new PopSingleMyStoreDataResponse(MyDistributedReadStackStore.MIDDLESTORE().pop(request.getMessage().getStackKey()));
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
