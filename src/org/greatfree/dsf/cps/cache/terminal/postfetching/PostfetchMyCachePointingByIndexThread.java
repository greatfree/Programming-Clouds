package org.greatfree.dsf.cps.cache.terminal.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexStream;
import org.greatfree.dsf.cps.cache.terminal.MySortedTerminalListStore;
import org.greatfree.dsf.cps.cache.terminal.MySortedTerminalMapStore;
import org.greatfree.dsf.cps.cache.terminal.MyTimingTerminalMapStore;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingByIndexThread extends RequestQueue<PostfetchMyCachePointingByIndexRequest, PostfetchMyCachePointingByIndexStream, PostfetchMyCachePointingByIndexResponse>
{

	public PostfetchMyCachePointingByIndexThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PostfetchMyCachePointingByIndexStream request;
		PostfetchMyCachePointingByIndexResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				if (!request.getMessage().isTiming())
				{
					if (request.getMessage().isTerminalMap())
					{
						response = new PostfetchMyCachePointingByIndexResponse(MySortedTerminalMapStore.BACKEND().get(request.getMessage().getMapKey(), request.getMessage().getIndex()));
					}
					else
					{
						response = new PostfetchMyCachePointingByIndexResponse(MySortedTerminalListStore.BACKEND().get(request.getMessage().getMapKey(), request.getMessage().getIndex()));
					}
				}
				else
				{
					response = new PostfetchMyCachePointingByIndexResponse(MyTimingTerminalMapStore.BACKEND().get(request.getMessage().getMapKey(), request.getMessage().getIndex()));
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
