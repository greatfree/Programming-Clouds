package org.greatfree.framework.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.framework.cps.cache.coordinator.MySortedDistributedCacheStore;
import org.greatfree.framework.cps.cache.coordinator.MyTimingDistributedCacheStore;
import org.greatfree.framework.cps.cache.message.front.TopCachePointingsRequest;
import org.greatfree.framework.cps.cache.message.front.TopCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.front.TopCachePointingsStream;
import org.greatfree.util.UtilConfig;

// Created: 07/24/2018, Bing Li
public class TopCachePointingsThread extends RequestQueue<TopCachePointingsRequest, TopCachePointingsStream, TopCachePointingsResponse>
{

	public TopCachePointingsThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		TopCachePointingsStream request;
		TopCachePointingsResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				try
				{
					if (!request.getMessage().isTiming())
					{
						response = new TopCachePointingsResponse(MySortedDistributedCacheStore.MIDDLESTORE().getTop(request.getMessage().getMapKey(), request.getMessage().getEndIndex()));
					}
					else
					{
						response = new TopCachePointingsResponse(UtilConfig.NO_KEY, MyTimingDistributedCacheStore.MIDDLESTORE().getTop(request.getMessage().getMapKey(), request.getMessage().getEndIndex()));
					}
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (IOException | DistributedListFetchException e)
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
