package org.greatfree.dip.cps.cache.terminal.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyStoreDataRequest;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyStoreDataResponse;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyStoreDataStream;
import org.greatfree.dip.cps.cache.terminal.MyTerminalMapStore;

// Created: 08/25/2018, Bing Li
public class PostfetchMyStoreDataThread extends RequestQueue<PostfetchMyStoreDataRequest, PostfetchMyStoreDataStream, PostfetchMyStoreDataResponse>
{

	public PostfetchMyStoreDataThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PostfetchMyStoreDataStream request;
		PostfetchMyStoreDataResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new PostfetchMyStoreDataResponse(MyTerminalMapStore.BACKEND().get(request.getMessage().getMapKey(), request.getMessage().getDataKey()));
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
