package org.greatfree.framework.cps.cache.terminal.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMinMyPointingRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMinMyPointingResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMinMyPointingStream;
import org.greatfree.framework.cps.cache.terminal.MySortedTerminalList;

// Created: 08/01/2018, Bing Li
public class PostfetchMinMyPointingRequestThread extends RequestQueue<PostfetchMinMyPointingRequest, PostfetchMinMyPointingStream, PostfetchMinMyPointingResponse>
{

	public PostfetchMinMyPointingRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PostfetchMinMyPointingStream request;
		PostfetchMinMyPointingResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new PostfetchMinMyPointingResponse(MySortedTerminalList.BACKEND().getMinPointing());
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
