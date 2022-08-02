package org.greatfree.framework.cps.cache.coordinator.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.coordinator.MySortedDistributedCacheStore;
import org.greatfree.framework.cps.cache.message.FetchMyCachePointingNotification;

// Created: 07/22/2018, Bing Li
public class PostfetchMyCachePointingThread extends NotificationObjectQueue<FetchMyCachePointingNotification>
{

	public PostfetchMyCachePointingThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		FetchMyCachePointingNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					MySortedDistributedCacheStore.MIDDLESTORE().postfetch(notification);
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
