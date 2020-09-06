package org.greatfree.dsf.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.MyDistributedReadStackStore;
import org.greatfree.dsf.cps.cache.coordinator.MyDistributedStackStore;
import org.greatfree.dsf.cps.cache.message.front.PeekMyStoreDataRequest;
import org.greatfree.dsf.cps.cache.message.front.PeekMyStoreDataResponse;
import org.greatfree.dsf.cps.cache.message.front.PeekMyStoreDataStream;
import org.greatfree.exceptions.DistributedListFetchException;
import org.greatfree.util.UtilConfig;

// Created: 08/09/2018, Bing Li
public class PeekMyStoreDataThread extends RequestQueue<PeekMyStoreDataRequest, PeekMyStoreDataStream, PeekMyStoreDataResponse>
{

	public PeekMyStoreDataThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PeekMyStoreDataStream request;
		PeekMyStoreDataResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				try
				{
					if (!request.getMessage().isReading())
					{
						if (request.getMessage().getStartIndex() == UtilConfig.NO_INDEX)
						{
							response = new PeekMyStoreDataResponse(MyDistributedStackStore.MIDDLESTORE().peekAll(request.getMessage().getStackKey(), request.getMessage().getCount()));
						}
						else
						{
							if (request.getMessage().getStartIndex() != request.getMessage().getEndIndex())
							{
								response = new PeekMyStoreDataResponse(MyDistributedStackStore.MIDDLESTORE().peekRange(request.getMessage().getStackKey(), request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
							}
							else
							{
								response = new PeekMyStoreDataResponse(MyDistributedStackStore.MIDDLESTORE().get(request.getMessage().getStackKey(), request.getMessage().getStartIndex()));
							}
						}
					}
					else
					{
						if (request.getMessage().getStartIndex() == UtilConfig.NO_INDEX)
						{
							response = new PeekMyStoreDataResponse(MyDistributedReadStackStore.MIDDLESTORE().peekAll(request.getMessage().getStackKey(), request.getMessage().getCount()));
						}
						else
						{
							if (request.getMessage().getStartIndex() != request.getMessage().getEndIndex())
							{
								response = new PeekMyStoreDataResponse(MyDistributedReadStackStore.MIDDLESTORE().peekRange(request.getMessage().getStackKey(), request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
							}
							else
							{
								response = new PeekMyStoreDataResponse(MyDistributedReadStackStore.MIDDLESTORE().get(request.getMessage().getStackKey(), request.getMessage().getStartIndex()));
							}
						}
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
