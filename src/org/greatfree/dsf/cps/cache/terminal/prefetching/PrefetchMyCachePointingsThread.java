package org.greatfree.dsf.cps.cache.terminal.prefetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.message.prefetch.PrefetchMyCachePointingsRequest;
import org.greatfree.dsf.cps.cache.message.prefetch.PrefetchMyCachePointingsResponse;
import org.greatfree.dsf.cps.cache.message.prefetch.PrefetchMyCachePointingsStream;
import org.greatfree.dsf.cps.cache.terminal.MySortedTerminalListStore;
import org.greatfree.dsf.cps.cache.terminal.MySortedTerminalMapStore;
import org.greatfree.dsf.cps.cache.terminal.MyTimingTerminalMapStore;
import org.greatfree.util.UtilConfig;

// Created: 07/25/2018, Bing Li
public class PrefetchMyCachePointingsThread extends RequestQueue<PrefetchMyCachePointingsRequest, PrefetchMyCachePointingsStream, PrefetchMyCachePointingsResponse>
{

	public PrefetchMyCachePointingsThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PrefetchMyCachePointingsStream request;
		PrefetchMyCachePointingsResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				if (!request.getMessage().isTiming())
				{
					if (request.getMessage().isTerminalMap())
					{
						response = new PrefetchMyCachePointingsResponse(MySortedTerminalMapStore.BACKEND().getRange(request.getMessage().getMapKey(), request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
					}
					else
					{
						response = new PrefetchMyCachePointingsResponse(MySortedTerminalListStore.BACKEND().getRange(request.getMessage().getMapKey(), request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
					}
				}
				else
				{
					response = new PrefetchMyCachePointingsResponse(UtilConfig.NO_KEY, MyTimingTerminalMapStore.BACKEND().getRange(request.getMessage().getMapKey(), request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
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
