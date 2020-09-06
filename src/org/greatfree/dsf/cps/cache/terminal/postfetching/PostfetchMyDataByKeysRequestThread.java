package org.greatfree.dsf.cps.cache.terminal.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataByKeysRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataByKeysResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataByKeysStream;
import org.greatfree.dsf.cps.cache.terminal.MyTerminalMap;

// Created: 07/21/2018, Bing Li
public class PostfetchMyDataByKeysRequestThread extends RequestQueue<PostfetchMyDataByKeysRequest, PostfetchMyDataByKeysStream, PostfetchMyDataByKeysResponse>
{

	public PostfetchMyDataByKeysRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PostfetchMyDataByKeysStream request;
		PostfetchMyDataByKeysResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new PostfetchMyDataByKeysResponse(MyTerminalMap.BACKEND().get(request.getMessage().getResourceKeys()));
				
				System.out.println("PostfetchMyDataByKeysRequestThread: retrieved data size = " + response.getData().size());
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
