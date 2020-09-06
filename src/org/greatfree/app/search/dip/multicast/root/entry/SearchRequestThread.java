package org.greatfree.app.search.dip.multicast.root.entry;

import java.io.IOException;

import org.greatfree.app.search.dip.multicast.message.SearchMultiRequest;
import org.greatfree.app.search.dip.multicast.message.SearchMultiResponse;
import org.greatfree.app.search.dip.multicast.message.SearchRequest;
import org.greatfree.app.search.dip.multicast.message.SearchResponse;
import org.greatfree.app.search.dip.multicast.message.SearchStream;
import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.root.RootMulticastor;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.util.Tools;

// Created: 09/28/2018, Bing Li
class SearchRequestThread extends RequestQueue<SearchRequest, SearchStream, SearchResponse>
{

	public SearchRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		SearchStream request;
		SearchResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				try
				{
					// Map/Reduce - Hadoop. 10/07/2018, Bing Li
					response = new SearchResponse(Tools.filter(RootMulticastor.ROOT().broadcastRead(new SearchMultiRequest(request.getMessage().getUserKey(), request.getMessage().getQuery())), SearchMultiResponse.class));
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (InstantiationException | IllegalAccessException | InterruptedException | IOException | DistributedNodeFailedException e)
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
