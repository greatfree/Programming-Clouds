package org.greatfree.framework.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MySortedDistributedMap;
import org.greatfree.framework.cps.cache.message.front.LoadMinMyPointingRequest;
import org.greatfree.framework.cps.cache.message.front.LoadMinMyPointingResponse;
import org.greatfree.framework.cps.cache.message.front.LoadMinMyPointingStream;

// Created: 07/20/2018, Bing Li
public class LoadMinMyPointingThread extends RequestQueue<LoadMinMyPointingRequest, LoadMinMyPointingStream, LoadMinMyPointingResponse>
{

	public LoadMinMyPointingThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		LoadMinMyPointingStream request;
		LoadMinMyPointingResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				response = new LoadMinMyPointingResponse(MySortedDistributedMap.MIDDLE().getMinPointing());
				
				System.out.println("LoadMinMyPointingThread: description = " + response.getPointing().getDescription());
				System.out.println("LoadMinMyPointingThread: points = " + response.getPointing().getPoints());
				
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
