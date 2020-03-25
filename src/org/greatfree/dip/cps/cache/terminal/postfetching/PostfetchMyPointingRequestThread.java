package org.greatfree.dip.cps.cache.terminal.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyPointingByKeyRequest;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyPointingByKeyResponse;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyPointingStream;
import org.greatfree.dip.cps.cache.terminal.MySortedTerminalList;

// Created: 07/12/2018, Bing Li
public class PostfetchMyPointingRequestThread extends RequestQueue<PostfetchMyPointingByKeyRequest, PostfetchMyPointingStream, PostfetchMyPointingByKeyResponse>
{

	public PostfetchMyPointingRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PostfetchMyPointingStream request;
		PostfetchMyPointingByKeyResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new PostfetchMyPointingByKeyResponse(MySortedTerminalList.BACKEND().getPointing(request.getMessage().getResourceKey()));
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
