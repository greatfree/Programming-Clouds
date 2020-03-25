package org.greatfree.dip.cps.cache.terminal.prefetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.message.prefetch.PrefetchMyPointingsRequest;
import org.greatfree.dip.cps.cache.message.prefetch.PrefetchMyPointingsResponse;
import org.greatfree.dip.cps.cache.message.prefetch.PrefetchMyPointingsStream;
import org.greatfree.dip.cps.cache.terminal.MySortedTerminalList;

// Created: 07/12/2018, Bing Li
public class PrefetchMyPointingsRequestThread extends RequestQueue<PrefetchMyPointingsRequest, PrefetchMyPointingsStream, PrefetchMyPointingsResponse>
{

	public PrefetchMyPointingsRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PrefetchMyPointingsStream request;
		PrefetchMyPointingsResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new PrefetchMyPointingsResponse(MySortedTerminalList.BACKEND().getPointings(request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
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
