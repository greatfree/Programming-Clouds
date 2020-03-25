package org.greatfree.dip.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.MyDistributedList;
import org.greatfree.dip.cps.cache.message.front.LoadRangeMyUKsRequest;
import org.greatfree.dip.cps.cache.message.front.LoadRangeMyUKsResponse;
import org.greatfree.dip.cps.cache.message.front.LoadRangeMyUKsStream;
import org.greatfree.exceptions.DistributedListFetchException;

// Created: 03/01/2019, Bing Li
public class LoadMyRangeUKsThread extends RequestQueue<LoadRangeMyUKsRequest, LoadRangeMyUKsStream, LoadRangeMyUKsResponse>
{

	public LoadMyRangeUKsThread(int notificationQueueSize)
	{
		super(notificationQueueSize);
	}

	@Override
	public void run()
	{
		LoadRangeMyUKsStream request;
		LoadRangeMyUKsResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				
				try
				{
					response = new LoadRangeMyUKsResponse(MyDistributedList.MIDDLE().getRange(request.getMessage().getStartIndex(), request.getMessage().getEndIndex()));
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
