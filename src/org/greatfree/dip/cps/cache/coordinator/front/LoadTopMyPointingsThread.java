package org.greatfree.dip.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.MySortedDistributedList;
import org.greatfree.dip.cps.cache.message.front.LoadTopMyPointingsRequest;
import org.greatfree.dip.cps.cache.message.front.LoadTopMyPointingsResponse;
import org.greatfree.dip.cps.cache.message.front.LoadTopMyPointingsStream;
import org.greatfree.exceptions.DistributedListFetchException;

// Created: 07/13/2018, Bing Li
public class LoadTopMyPointingsThread extends RequestQueue<LoadTopMyPointingsRequest, LoadTopMyPointingsStream, LoadTopMyPointingsResponse>
{

	public LoadTopMyPointingsThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		LoadTopMyPointingsStream request;
		LoadTopMyPointingsResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				/*
				List<MyPointing> pointings = MyPointingDistributedList.MIDDLE().getTop(request.getMessage().getEndIndex());
				for (MyPointing entry : pointings)
				{
					System.out.println(entry.getKey() + ", " + entry.getPoints() + ", " + entry.getDescription());
				}
				*/
				try
				{
					response = new LoadTopMyPointingsResponse(MySortedDistributedList.MIDDLE().getTop(request.getMessage().getEndIndex()));
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (IOException | DistributedListFetchException e)
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
