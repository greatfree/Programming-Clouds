package org.greatfree.framework.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedMapStore;
import org.greatfree.framework.cps.cache.message.front.LoadMapStoreDataKeysRequest;
import org.greatfree.framework.cps.cache.message.front.LoadMapStoreDataKeysResponse;
import org.greatfree.framework.cps.cache.message.front.LoadMapStoreDataKeysStream;

// Created: 08/25/2018, Bing Li
public class LoadMapStoreDataKeysThread extends RequestQueue<LoadMapStoreDataKeysRequest, LoadMapStoreDataKeysStream, LoadMapStoreDataKeysResponse>
{

	public LoadMapStoreDataKeysThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		LoadMapStoreDataKeysStream request;
		LoadMapStoreDataKeysResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				/*
				System.out.println("LoadMapStoreDataKeysThread: mapKey = " + request.getMessage().getMapKey());
				Set<String> keys = Sets.newHashSet();
				for (int i = 0; i < 10; i++)
				{
					keys.add("Key" + i);
				}
				*/
				response = new LoadMapStoreDataKeysResponse(MyDistributedMapStore.MIDDLE().getDataKeys(request.getMessage().getMapKey()));
//				response = new LoadMapStoreDataKeysResponse(keys);
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
