package org.greatfree.framework.cps.cache.coordinator.prefetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedStackStore;
import org.greatfree.framework.cps.cache.message.FetchStackNotification;

// Created: 08/07/2018, Bing Li
public class PrefetchStackThread extends NotificationObjectQueue<FetchStackNotification>
{

	public PrefetchStackThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		FetchStackNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					MyDistributedStackStore.MIDDLESTORE().prefetch(notification);
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
