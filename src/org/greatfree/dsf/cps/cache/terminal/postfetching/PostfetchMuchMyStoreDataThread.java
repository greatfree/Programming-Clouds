package org.greatfree.dsf.cps.cache.terminal.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMuchMyStoreDataRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMuchMyStoreDataResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMuchMyStoreDataStream;
import org.greatfree.dsf.cps.cache.terminal.MyTerminalMapStore;

// Created: 08/25/2018, Bing Li
public class PostfetchMuchMyStoreDataThread extends RequestQueue<PostfetchMuchMyStoreDataRequest, PostfetchMuchMyStoreDataStream, PostfetchMuchMyStoreDataResponse>
{

	public PostfetchMuchMyStoreDataThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PostfetchMuchMyStoreDataStream request;
		PostfetchMuchMyStoreDataResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new PostfetchMuchMyStoreDataResponse(MyTerminalMapStore.BACKEND().get(request.getMessage().getMapKey(), request.getMessage().getKeys()));
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
