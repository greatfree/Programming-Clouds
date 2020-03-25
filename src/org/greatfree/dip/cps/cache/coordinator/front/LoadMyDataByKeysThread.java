package org.greatfree.dip.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.MyDistributedMap;
import org.greatfree.dip.cps.cache.coordinator.MyDistributedMapStore;
import org.greatfree.dip.cps.cache.coordinator.MyReadDistributedMap;
import org.greatfree.dip.cps.cache.message.front.LoadMyDataByKeysRequest;
import org.greatfree.dip.cps.cache.message.front.LoadMyDataByKeysResponse;
import org.greatfree.dip.cps.cache.message.front.LoadMyDataByKeysStream;
import org.greatfree.exceptions.DistributedMapFetchException;
import org.greatfree.util.UtilConfig;

// Created: 07/21/2018, Bing Li
public class LoadMyDataByKeysThread extends RequestQueue<LoadMyDataByKeysRequest, LoadMyDataByKeysStream, LoadMyDataByKeysResponse>
{

	public LoadMyDataByKeysThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		LoadMyDataByKeysStream request;
		LoadMyDataByKeysResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				try
				{
					if (!request.getMessage().isPostMap())
					{
						if (request.getMessage().getMapKey() != null)
						{
							response = new LoadMyDataByKeysResponse(UtilConfig.NO_KEY, MyDistributedMapStore.MIDDLE().getData(request.getMessage().getMapKey(), request.getMessage().getResourceKeys()));
						}
						else
						{
							response = new LoadMyDataByKeysResponse(MyDistributedMap.MIDDLE().get(request.getMessage().getResourceKeys()));
						}
					}
					else
					{
						response = new LoadMyDataByKeysResponse(MyReadDistributedMap.MIDDLE().get(request.getMessage().getResourceKeys()));
					}
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (DistributedMapFetchException | IOException e)
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
