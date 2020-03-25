package org.greatfree.dip.cps.cache.terminal.prefetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.message.prefetch.PrefetchMyUKValuesRequest;
import org.greatfree.dip.cps.cache.message.prefetch.PrefetchMyUKValuesResponse;
import org.greatfree.dip.cps.cache.message.prefetch.PrefetchMyUKValuesStream;
import org.greatfree.dip.cps.cache.terminal.MyTerminalList;

// Created: 02/27/2019, Bing Li
public class PrefetchMyUKValuesThread extends RequestQueue<PrefetchMyUKValuesRequest, PrefetchMyUKValuesStream, PrefetchMyUKValuesResponse>
{

	public PrefetchMyUKValuesThread(int notificationQueueSize)
	{
		super(notificationQueueSize);
	}

	@Override
	public void run()
	{
		PrefetchMyUKValuesStream request;
		PrefetchMyUKValuesResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new PrefetchMyUKValuesResponse(MyTerminalList.BACKEND().getRange(request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
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
