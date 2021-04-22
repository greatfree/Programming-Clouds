package org.greatfree.framework.cps.cache.terminal.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataKeysRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataKeysResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataKeysStream;
import org.greatfree.framework.cps.cache.terminal.MyTerminalMapStore;

// Created: 08/25/2018, Bing Li
public class PostfetchMyStoreDataKeysThread extends RequestQueue<PostfetchMyStoreDataKeysRequest, PostfetchMyStoreDataKeysStream, PostfetchMyStoreDataKeysResponse>
{

	public PostfetchMyStoreDataKeysThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PostfetchMyStoreDataKeysStream request;
		PostfetchMyStoreDataKeysResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new PostfetchMyStoreDataKeysResponse(MyTerminalMapStore.BACKEND().getValues(request.getMessage().getMapKey(), request.getMessage().getSize()));
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
