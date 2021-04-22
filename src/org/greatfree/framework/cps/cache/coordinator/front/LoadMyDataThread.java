package org.greatfree.framework.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedMap;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedMapStore;
import org.greatfree.framework.cps.cache.coordinator.MyReadDistributedMap;
import org.greatfree.framework.cps.cache.message.front.LoadMyDataRequest;
import org.greatfree.framework.cps.cache.message.front.LoadMyDataResponse;
import org.greatfree.framework.cps.cache.message.front.LoadMyDataStream;

// Created: 07/09/2018, Bing Li
public class LoadMyDataThread extends RequestQueue<LoadMyDataRequest, LoadMyDataStream, LoadMyDataResponse>
{

	public LoadMyDataThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		LoadMyDataStream request;
		LoadMyDataResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
//				System.out.println("1) LoadMyDataThread: " + request.getMessage().getMyDataKey());
				if (!request.getMessage().isPostMap())
				{
					if (request.getMessage().getMapKey() != null)
					{
						response = new LoadMyDataResponse(MyDistributedMapStore.MIDDLE().getData(request.getMessage().getMapKey(), request.getMessage().getMyDataKey()));
					}
					else
					{
						response = new LoadMyDataResponse(MyDistributedMap.MIDDLE().get(request.getMessage().getMyDataKey()));
					}
				}
				else
				{
					response = new LoadMyDataResponse(MyReadDistributedMap.MIDDLE().get(request.getMessage().getMyDataKey()));
				}
//				System.out.println("2) LoadMyDataThread: " + request.getMessage().getMyDataKey());
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
