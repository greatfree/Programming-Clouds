package org.greatfree.framework.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.framework.cps.cache.coordinator.MySortedDistributedReadCacheStore;
import org.greatfree.framework.cps.cache.message.front.RangeReadCachePointingsRequest;
import org.greatfree.framework.cps.cache.message.front.RangeReadCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.front.RangeReadCachePointingsStream;

// Created: 08/05/2018, Bing Li
public class LoadRangeReadMyPointingsThread extends RequestQueue<RangeReadCachePointingsRequest, RangeReadCachePointingsStream, RangeReadCachePointingsResponse>
{

	public LoadRangeReadMyPointingsThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		RangeReadCachePointingsStream request;
		RangeReadCachePointingsResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				try
				{
					response = new RangeReadCachePointingsResponse(MySortedDistributedReadCacheStore.MIDDLESTORE().getRange(request.getMessage().getMapKey(), request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
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
