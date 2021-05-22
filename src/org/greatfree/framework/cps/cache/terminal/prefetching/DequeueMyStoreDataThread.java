package org.greatfree.framework.cps.cache.terminal.prefetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.message.prefetch.DequeueMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.prefetch.DequeueMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.prefetch.DequeueMyStoreDataStream;
import org.greatfree.framework.cps.cache.terminal.MyTerminalQueueStore;
import org.greatfree.util.UtilConfig;

// Created: 08/13/2018, Bing Li
public class DequeueMyStoreDataThread extends RequestQueue<DequeueMyStoreDataRequest, DequeueMyStoreDataStream, DequeueMyStoreDataResponse>
{

	public DequeueMyStoreDataThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		DequeueMyStoreDataStream request;
		DequeueMyStoreDataResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				try
				{
					if (!request.getMessage().isPeeking())
					{
						response = new DequeueMyStoreDataResponse(MyTerminalQueueStore.BACKEND().dequeueAll(request.getMessage().getQueueKey(), request.getMessage().getCount()));
					}
					else
					{
						if (request.getMessage().getStartIndex() != UtilConfig.NO_INDEX)
						{
							response = new DequeueMyStoreDataResponse(MyTerminalQueueStore.BACKEND().peekRange(request.getMessage().getQueueKey(), request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
						}
						else
						{
							response = new DequeueMyStoreDataResponse(MyTerminalQueueStore.BACKEND().peekAll(request.getMessage().getQueueKey(), request.getMessage().getCount()));
						}

					}
					this.respond(request.getOutStream(), request.getLock(), response);
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
