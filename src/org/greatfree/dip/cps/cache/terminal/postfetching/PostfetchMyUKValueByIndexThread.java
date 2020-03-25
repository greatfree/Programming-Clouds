package org.greatfree.dip.cps.cache.terminal.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyUKValueByIndexRequest;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyUKValueByIndexResponse;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyUKValueByIndexStream;
import org.greatfree.dip.cps.cache.terminal.MyTerminalList;

// Created: 02/27/2019, Bing Li
public class PostfetchMyUKValueByIndexThread extends RequestQueue<PostfetchMyUKValueByIndexRequest, PostfetchMyUKValueByIndexStream, PostfetchMyUKValueByIndexResponse>
{

	public PostfetchMyUKValueByIndexThread(int notificationQueueSize)
	{
		super(notificationQueueSize);
	}

	@Override
	public void run()
	{
		PostfetchMyUKValueByIndexStream request;
		PostfetchMyUKValueByIndexResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new PostfetchMyUKValueByIndexResponse(MyTerminalList.BACKEND().get(request.getMessage().getIndex()));
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
