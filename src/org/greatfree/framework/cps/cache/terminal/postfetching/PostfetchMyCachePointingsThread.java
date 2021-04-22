package org.greatfree.framework.cps.cache.terminal.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingsRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingsStream;
import org.greatfree.framework.cps.cache.terminal.MySortedTerminalListStore;
import org.greatfree.framework.cps.cache.terminal.MySortedTerminalMapStore;
import org.greatfree.framework.cps.cache.terminal.MyTimingTerminalMapStore;
import org.greatfree.util.UtilConfig;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingsThread extends RequestQueue<PostfetchMyCachePointingsRequest, PostfetchMyCachePointingsStream, PostfetchMyCachePointingsResponse>
{

	public PostfetchMyCachePointingsThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PostfetchMyCachePointingsStream request;
		PostfetchMyCachePointingsResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				if (!request.getMessage().isTiming())
				{
					if (request.getMessage().isTerminalMap())
					{
						response = new PostfetchMyCachePointingsResponse(MySortedTerminalMapStore.BACKEND().getRange(request.getMessage().getMapKey(), request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
					}
					else
					{
						response = new PostfetchMyCachePointingsResponse(MySortedTerminalListStore.BACKEND().getRange(request.getMessage().getMapKey(), request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
					}
				}
				else
				{
					response = new PostfetchMyCachePointingsResponse(UtilConfig.NO_KEY, MyTimingTerminalMapStore.BACKEND().getRange(request.getMessage().getMapKey(), request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
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
