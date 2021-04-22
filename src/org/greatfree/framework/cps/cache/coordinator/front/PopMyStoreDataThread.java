package org.greatfree.framework.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedReadStackStore;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedStackStore;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataStream;

// Created: 08/09/2018, Bing Li
public class PopMyStoreDataThread extends RequestQueue<PopMyStoreDataRequest, PopMyStoreDataStream, PopMyStoreDataResponse>
{

	public PopMyStoreDataThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PopMyStoreDataStream request;
		PopMyStoreDataResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				try
				{
					System.out.println("PopMyStoreDataThread: stackKey = " + request.getMessage().getStackKey() + ", count = " + request.getMessage().getCount());
					if (!request.getMessage().isReading())
					{
						response = new PopMyStoreDataResponse(MyDistributedStackStore.MIDDLESTORE().popAll(request.getMessage().getStackKey(), request.getMessage().getCount()));
					}
					else
					{
						response = new PopMyStoreDataResponse(MyDistributedReadStackStore.MIDDLESTORE().popAll(request.getMessage().getStackKey(), request.getMessage().getCount()));
					}
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
