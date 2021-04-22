package org.greatfree.framework.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MySortedDistributedList;
import org.greatfree.framework.cps.cache.message.front.LoadMyPointingRequest;
import org.greatfree.framework.cps.cache.message.front.LoadMyPointingResponse;
import org.greatfree.framework.cps.cache.message.front.LoadMyPointingStream;

// Created: 07/13/2018, Bing Li
public class LoadMyPointingThread extends RequestQueue<LoadMyPointingRequest, LoadMyPointingStream, LoadMyPointingResponse>
{

	public LoadMyPointingThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		LoadMyPointingStream request;
		LoadMyPointingResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new LoadMyPointingResponse(MySortedDistributedList.MIDDLE().get(request.getMessage().getIndex()));
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
