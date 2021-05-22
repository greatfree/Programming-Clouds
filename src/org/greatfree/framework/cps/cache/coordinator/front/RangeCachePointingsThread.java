package org.greatfree.framework.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.framework.cps.cache.coordinator.MySortedDistributedCacheStore;
import org.greatfree.framework.cps.cache.coordinator.MyTimingDistributedCacheStore;
import org.greatfree.framework.cps.cache.message.front.RangeCachePointingsRequest;
import org.greatfree.framework.cps.cache.message.front.RangeCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.front.RangeCachePointingsStream;
import org.greatfree.util.UtilConfig;

// Created: 07/24/2018, Bing Li
public class RangeCachePointingsThread extends RequestQueue<RangeCachePointingsRequest, RangeCachePointingsStream, RangeCachePointingsResponse>
{

	public RangeCachePointingsThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		RangeCachePointingsStream request;
		RangeCachePointingsResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				try
				{
					if (!request.getMessage().isTiming())
					{
						response = new RangeCachePointingsResponse(MySortedDistributedCacheStore.MIDDLESTORE().getRange(request.getMessage().getMapKey(), request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
					}
					else
					{
						response = new RangeCachePointingsResponse(UtilConfig.NO_KEY, MyTimingDistributedCacheStore.MIDDLESTORE().getRange(request.getMessage().getMapKey(), request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
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
