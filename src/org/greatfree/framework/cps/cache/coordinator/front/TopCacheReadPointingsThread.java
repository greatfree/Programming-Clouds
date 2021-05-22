package org.greatfree.framework.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.framework.cps.cache.coordinator.MySortedDistributedReadCacheStore;
import org.greatfree.framework.cps.cache.message.front.TopReadCachePointingsRequest;
import org.greatfree.framework.cps.cache.message.front.TopReadCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.front.TopReadCachePointingsStream;

// Created: 08/05/2018, Bing Li
public class TopCacheReadPointingsThread extends RequestQueue<TopReadCachePointingsRequest, TopReadCachePointingsStream, TopReadCachePointingsResponse>
{

	public TopCacheReadPointingsThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		TopReadCachePointingsStream request;
		TopReadCachePointingsResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				try
				{
					response = new TopReadCachePointingsResponse(MySortedDistributedReadCacheStore.MIDDLESTORE().getTop(request.getMessage().getMapKey(), request.getMessage().getEndIndex()));
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
