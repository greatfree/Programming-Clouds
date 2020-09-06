package org.greatfree.dsf.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.MySortedPrefetchListStore;
import org.greatfree.dsf.cps.cache.message.front.PointingByIndexPrefetchListRequest;
import org.greatfree.dsf.cps.cache.message.front.PointingByIndexPrefetchListResponse;
import org.greatfree.dsf.cps.cache.message.front.PointingByIndexPrefetchListStream;

// Created: 08/03/2018, Bing Li
public class PointingByIndexPrefetchListThread extends RequestQueue<PointingByIndexPrefetchListRequest, PointingByIndexPrefetchListStream, PointingByIndexPrefetchListResponse>
{

	public PointingByIndexPrefetchListThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PointingByIndexPrefetchListStream request;
		PointingByIndexPrefetchListResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new PointingByIndexPrefetchListResponse(MySortedPrefetchListStore.MIDDLE().get(request.getMessage().getListKey(), request.getMessage().getIndex()));
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
