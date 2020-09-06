package org.greatfree.dsf.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.MySortedPrefetchListStore;
import org.greatfree.dsf.cps.cache.message.front.RangePointingsPrefetchListRequest;
import org.greatfree.dsf.cps.cache.message.front.RangePointingsPrefetchListResponse;
import org.greatfree.dsf.cps.cache.message.front.RangePointingsPrefetchListStream;

// Created: 08/03/2018, Bing Li
public class RangePointingsPrefetchListThread extends RequestQueue<RangePointingsPrefetchListRequest, RangePointingsPrefetchListStream, RangePointingsPrefetchListResponse>
{

	public RangePointingsPrefetchListThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		RangePointingsPrefetchListStream request;
		RangePointingsPrefetchListResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new RangePointingsPrefetchListResponse(MySortedPrefetchListStore.MIDDLE().getRange(request.getMessage().getMapKey(), request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
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
