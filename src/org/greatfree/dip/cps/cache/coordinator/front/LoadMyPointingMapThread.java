package org.greatfree.dip.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.MySortedDistributedMap;
import org.greatfree.dip.cps.cache.message.front.LoadMyPointingMapRequest;
import org.greatfree.dip.cps.cache.message.front.LoadMyPointingMapResponse;
import org.greatfree.dip.cps.cache.message.front.LoadMyPointingMapStream;

// Created: 07/20/2018, Bing Li
public class LoadMyPointingMapThread extends RequestQueue<LoadMyPointingMapRequest, LoadMyPointingMapStream, LoadMyPointingMapResponse>
{

	public LoadMyPointingMapThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		LoadMyPointingMapStream request;
		LoadMyPointingMapResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new LoadMyPointingMapResponse(MySortedDistributedMap.MIDDLE().getPointing(request.getMessage().getResourceKey()));
				try
				{
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
