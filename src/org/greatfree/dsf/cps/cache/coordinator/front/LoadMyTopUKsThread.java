package org.greatfree.dsf.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.MyDistributedList;
import org.greatfree.dsf.cps.cache.message.front.LoadTopMyUKsRequest;
import org.greatfree.dsf.cps.cache.message.front.LoadTopMyUKsResponse;
import org.greatfree.dsf.cps.cache.message.front.LoadTopMyUKsStream;
import org.greatfree.exceptions.DistributedListFetchException;

// Created: 03/01/2019, Bing Li
public class LoadMyTopUKsThread extends RequestQueue<LoadTopMyUKsRequest, LoadTopMyUKsStream, LoadTopMyUKsResponse>
{

	public LoadMyTopUKsThread(int notificationQueueSize)
	{
		super(notificationQueueSize);
	}

	@Override
	public void run()
	{
		LoadTopMyUKsStream request;
		LoadTopMyUKsResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				try
				{
					response = new LoadTopMyUKsResponse(MyDistributedList.MIDDLE().getTop(request.getMessage().getEndIndex()));
					
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
