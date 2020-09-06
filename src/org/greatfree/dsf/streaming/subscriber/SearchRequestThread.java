package org.greatfree.dsf.streaming.subscriber;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.streaming.message.SearchRequest;
import org.greatfree.dsf.streaming.message.SearchResponse;
import org.greatfree.dsf.streaming.message.SearchStream;

// Created: 03/21/2020, Bing Li
public class SearchRequestThread extends RequestQueue<SearchRequest, SearchStream, SearchResponse>
{

	public SearchRequestThread(int taskSize)
	{
		super(taskSize);
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
				try
				{
					request = this.getRequest();
					response = new SearchResponse(SubscriberDB.DB().search(request.getMessage().getQuery()));
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

}
