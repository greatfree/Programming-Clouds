package org.greatfree.framework.cps.cache.terminal.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyStream;
import org.greatfree.framework.cps.cache.terminal.MySortedTerminalMapStore;
import org.greatfree.framework.cps.cache.terminal.MyTimingTerminalMapStore;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingByKeyThread extends RequestQueue<PostfetchMyCachePointingByKeyRequest, PostfetchMyCachePointingByKeyStream, PostfetchMyCachePointingByKeyResponse>
{

	public PostfetchMyCachePointingByKeyThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PostfetchMyCachePointingByKeyStream request;
		PostfetchMyCachePointingByKeyResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				if (!request.getMessage().isTiming())
				{
					response = new PostfetchMyCachePointingByKeyResponse(MySortedTerminalMapStore.BACKEND().get(request.getMessage().getMapKey(), request.getMessage().getResourceKey()));
				}
				else
				{
					response = new PostfetchMyCachePointingByKeyResponse(MyTimingTerminalMapStore.BACKEND().get(request.getMessage().getMapKey(), request.getMessage().getResourceKey()));
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
