package org.greatfree.dsf.cps.cache.coordinator.postfetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.MySortedDistributedReadCacheStore;
import org.greatfree.dsf.cps.cache.message.FetchMyCachePointingNotification;
import org.greatfree.exceptions.RemoteReadException;

// Created: 08/23/2018, Bing Li
public class PostfetchMyCacheReadPointingThread extends NotificationObjectQueue<FetchMyCachePointingNotification>
{

	public PostfetchMyCacheReadPointingThread(int taskSize)
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
					notification = this.getNotification();
					MySortedDistributedReadCacheStore.MIDDLESTORE().postfetch(notification);
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
