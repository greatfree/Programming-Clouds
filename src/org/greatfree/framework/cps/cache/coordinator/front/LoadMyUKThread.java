package org.greatfree.framework.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedList;
import org.greatfree.framework.cps.cache.message.front.LoadMyUKRequest;
import org.greatfree.framework.cps.cache.message.front.LoadMyUKResponse;
import org.greatfree.framework.cps.cache.message.front.LoadMyUKStream;

// Created: 03/01/2019, Bing Li
public class LoadMyUKThread extends RequestQueue<LoadMyUKRequest, LoadMyUKStream, LoadMyUKResponse>
{

	public LoadMyUKThread(int notificationQueueSize)
	{
		super(notificationQueueSize);
	}

	@Override
	public void run()
	{
		LoadMyUKStream request;
		LoadMyUKResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				
				response = new LoadMyUKResponse(MyDistributedList.MIDDLE().get(request.getMessage().getIndex()));
				
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
