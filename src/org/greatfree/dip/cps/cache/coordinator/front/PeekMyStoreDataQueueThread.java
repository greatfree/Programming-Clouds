package org.greatfree.dip.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.MyDistributedQueueStore;
import org.greatfree.dip.cps.cache.message.front.PeekMyStoreDataQueueRequest;
import org.greatfree.dip.cps.cache.message.front.PeekMyStoreDataQueueResponse;
import org.greatfree.dip.cps.cache.message.front.PeekMyStoreDataQueueStream;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.util.UtilConfig;

// Created: 08/14/2018, Bing Li
public class PeekMyStoreDataQueueThread extends RequestQueue<PeekMyStoreDataQueueRequest, PeekMyStoreDataQueueStream, PeekMyStoreDataQueueResponse>
{

	public PeekMyStoreDataQueueThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PeekMyStoreDataQueueStream request;
		PeekMyStoreDataQueueResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				try
				{
					if (request.getMessage().getStartIndex() != UtilConfig.NO_INDEX)
					{
						if (request.getMessage().getStartIndex() != request.getMessage().getEndIndex())
						{
							response = new PeekMyStoreDataQueueResponse(MyDistributedQueueStore.MIDDLESTORE().peekRange(request.getMessage().getQueueKey(), request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
						}
						else
						{
							response = new PeekMyStoreDataQueueResponse(MyDistributedQueueStore.MIDDLESTORE().get(request.getMessage().getQueueKey(), request.getMessage().getStartIndex()));
						}
					}
					else
					{
						response = new PeekMyStoreDataQueueResponse(MyDistributedQueueStore.MIDDLESTORE().peekAll(request.getMessage().getQueueKey(), request.getMessage().getCount()));
					}
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (DistributedListFetchException | IOException e)
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
