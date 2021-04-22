package org.greatfree.framework.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MySortedDistributedMap;
import org.greatfree.framework.cps.cache.message.front.LoadMaxMyPointingRequest;
import org.greatfree.framework.cps.cache.message.front.LoadMaxMyPointingResponse;
import org.greatfree.framework.cps.cache.message.front.LoadMaxMyPointingStream;

// Created: 07/20/2018, Bing Li
public class LoadMaxMyPointingThread extends RequestQueue<LoadMaxMyPointingRequest, LoadMaxMyPointingStream, LoadMaxMyPointingResponse>
{

	public LoadMaxMyPointingThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		LoadMaxMyPointingStream request;
		LoadMaxMyPointingResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new LoadMaxMyPointingResponse(MySortedDistributedMap.MIDDLE().getMaxPointing());
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
