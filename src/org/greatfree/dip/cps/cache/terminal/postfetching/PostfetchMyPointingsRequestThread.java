package org.greatfree.dip.cps.cache.terminal.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyPointingsRequest;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyPointingsResponse;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyPointingsStream;
import org.greatfree.dip.cps.cache.terminal.MySortedTerminalList;

// Created: 07/13/2018, Bing Li
public class PostfetchMyPointingsRequestThread extends RequestQueue<PostfetchMyPointingsRequest, PostfetchMyPointingsStream, PostfetchMyPointingsResponse>
{

	public PostfetchMyPointingsRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PostfetchMyPointingsStream request;
		PostfetchMyPointingsResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
//				response = new PostfetchMyPointingsResponse(MyPointingTerminalList.BACKEND().getPointings(request.getMessage().getIndex(), request.getMessage().getIndex() + request.getMessage().getPostfetchCount() - 1));
				/*
				List<MyPointing> pointings = MyPointingTerminalList.BACKEND().getPointings(request.getMessage().getStartIndex(), request.getMessage().getEndIndex());
				for (MyPointing entry : pointings)
				{
					System.out.println(entry.getKey() + ", " + entry.getPoints() + ", " + entry.getDescription());
				}
				*/
				response = new PostfetchMyPointingsResponse(MySortedTerminalList.BACKEND().getPointings(request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
//				response = new PostfetchMyPointingsResponse(pointings);
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
