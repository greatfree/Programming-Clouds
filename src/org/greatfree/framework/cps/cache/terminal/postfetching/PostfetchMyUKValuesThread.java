package org.greatfree.framework.cps.cache.terminal.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyUKValuesRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyUKValuesResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyUKValuesStream;
import org.greatfree.framework.cps.cache.terminal.MyTerminalList;

// Created: 02/27/2019, Bing Li
public class PostfetchMyUKValuesThread extends RequestQueue<PostfetchMyUKValuesRequest, PostfetchMyUKValuesStream, PostfetchMyUKValuesResponse>
{

	public PostfetchMyUKValuesThread(int notificationQueueSize)
	{
		super(notificationQueueSize);
	}

	@Override
	public void run()
	{
		PostfetchMyUKValuesStream request;
		PostfetchMyUKValuesResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				response = new PostfetchMyUKValuesResponse(MyTerminalList.BACKEND().getRange(request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
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
