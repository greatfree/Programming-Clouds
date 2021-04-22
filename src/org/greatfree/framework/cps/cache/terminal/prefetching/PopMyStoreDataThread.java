package org.greatfree.framework.cps.cache.terminal.prefetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataStream;
import org.greatfree.framework.cps.cache.terminal.MyTerminalStackStore;
import org.greatfree.util.UtilConfig;

// Created: 08/09/2018, Bing Li
public class PopMyStoreDataThread extends RequestQueue<PopMyStoreDataRequest, PopMyStoreDataStream, PopMyStoreDataResponse>
{

	public PopMyStoreDataThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PopMyStoreDataStream request;
		PopMyStoreDataResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
//				System.out.println("PopMyStoreDataThread: stack key = " + request.getMessage().getStackKey() + ", count = " + request.getMessage().getCount());
				try
				{
					if (!request.getMessage().isPeeking())
					{
						response = new PopMyStoreDataResponse(MyTerminalStackStore.BACKEND().popAll(request.getMessage().getStackKey(), request.getMessage().getCount()));
					}
					else
					{
						if (request.getMessage().getStartIndex() != UtilConfig.NO_INDEX)
						{
							System.out.println("PopMyStoreDataThread: stack key = " + request.getMessage().getStackKey() + ", startIndex = " + request.getMessage().getStartIndex() + ", endIndex = " + request.getMessage().getEndIndex());
							response = new PopMyStoreDataResponse(MyTerminalStackStore.BACKEND().peekRange(request.getMessage().getStackKey(), request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
						}
						else
						{
							response = new PopMyStoreDataResponse(MyTerminalStackStore.BACKEND().peekAll(request.getMessage().getStackKey(), request.getMessage().getCount()));
						}
					}
					if (response.getData() != null)
					{
						System.out.println("PopMyStoreDataThread: popped count = " + response.getData().size());
					}
					else
					{
						System.out.println("PopMyStoreDataThread: popped data is NULL!");
					}
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
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
