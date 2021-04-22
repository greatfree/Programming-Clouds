package org.greatfree.framework.cps.cache.terminal.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByIndexRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByIndexResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByIndexStream;
import org.greatfree.framework.cps.cache.terminal.MySortedTerminalList;

// Created: 07/16/2018, Bing Li
public class PostfetchMyPointingByIndexRequestThread extends RequestQueue<PostfetchMyPointingByIndexRequest, PostfetchMyPointingByIndexStream, PostfetchMyPointingByIndexResponse>
{

	public PostfetchMyPointingByIndexRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PostfetchMyPointingByIndexStream request;
		PostfetchMyPointingByIndexResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				System.out.println("PostfetchMyPointingByIndexRequestThread: index = " + request.getMessage().getIndex());
				response = new PostfetchMyPointingByIndexResponse(MySortedTerminalList.BACKEND().getPointing(request.getMessage().getIndex()));
				if (response.getPointing() != null)
				{
					System.out.println("PostfetchMyPointingByIndexRequestThread: response data = " + response.getPointing().getKey() + ", " + response.getPointing().getPoints() + ", " + response.getPointing().getDescription());
				}
				else
				{
					System.out.println("PostfetchMyPointingByIndexRequestThread: response data = NULL");
				}
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
