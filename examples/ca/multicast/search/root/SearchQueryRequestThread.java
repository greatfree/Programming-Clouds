package ca.multicast.search.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;

import ca.multicast.search.message.ClientSearchQueryRequest;
import ca.multicast.search.message.ClientSearchQueryResponse;
import ca.multicast.search.message.ClientSearchQueryStream;
import ca.multicast.search.message.SearchQueryRequest;

// Created: 03/15/2020, Bing Li
class SearchQueryRequestThread extends RequestQueue<ClientSearchQueryRequest, ClientSearchQueryStream, ClientSearchQueryResponse>
{

	public SearchQueryRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ClientSearchQueryStream request;
		ClientSearchQueryResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				try
				{
					response = new ClientSearchQueryResponse(SearchMulticastor.ROOT().broadRead(new SearchQueryRequest(request.getMessage().getQuery())));
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (DistributedNodeFailedException | IOException e)
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
