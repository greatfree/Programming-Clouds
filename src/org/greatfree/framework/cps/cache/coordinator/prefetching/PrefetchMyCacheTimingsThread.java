package org.greatfree.framework.cps.cache.coordinator.prefetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.coordinator.MyTimingDistributedCacheStore;
import org.greatfree.framework.cps.cache.message.FetchMyCacheTimingNotification;

// Created: 08/19/2018, Bing Li
public class PrefetchMyCacheTimingsThread extends NotificationObjectQueue<FetchMyCacheTimingNotification>
{

	public PrefetchMyCacheTimingsThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		FetchMyCacheTimingNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					MyTimingDistributedCacheStore.MIDDLESTORE().prefetch(notification);
					this.disposeObject(notification);
				}
				catch (InterruptedException | ClassNotFoundException | RemoteReadException | IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

}
