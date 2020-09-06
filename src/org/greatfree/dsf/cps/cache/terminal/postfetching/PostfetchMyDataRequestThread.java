package org.greatfree.dsf.cps.cache.terminal.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataStream;
import org.greatfree.dsf.cps.cache.terminal.MyTerminalMap;

// Created: 07/09/2018, Bing Li
public class PostfetchMyDataRequestThread extends RequestQueue<PostfetchMyDataRequest, PostfetchMyDataStream, PostfetchMyDataResponse>
{

	public PostfetchMyDataRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PostfetchMyDataStream request;
		PostfetchMyDataResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new PostfetchMyDataResponse(MyTerminalMap.BACKEND().get(request.getMessage().getMyDataKey()));
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
