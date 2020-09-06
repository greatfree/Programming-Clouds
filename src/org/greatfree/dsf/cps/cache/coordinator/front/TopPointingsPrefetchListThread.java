package org.greatfree.dsf.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.MySortedPrefetchListStore;
import org.greatfree.dsf.cps.cache.message.front.TopPointingsPrefetchListRequest;
import org.greatfree.dsf.cps.cache.message.front.TopPointingsPrefetchListResponse;
import org.greatfree.dsf.cps.cache.message.front.TopPointingsPrefetchListStream;

// Created: 08/03/2018, Bing Li
public class TopPointingsPrefetchListThread extends RequestQueue<TopPointingsPrefetchListRequest, TopPointingsPrefetchListStream, TopPointingsPrefetchListResponse>
{

	public TopPointingsPrefetchListThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		TopPointingsPrefetchListStream request;
		TopPointingsPrefetchListResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new TopPointingsPrefetchListResponse(MySortedPrefetchListStore.MIDDLE().getTop(request.getMessage().getListKey(), request.getMessage().getEndIndex()));
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
