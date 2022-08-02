package org.greatfree.framework.cps.cache.coordinator.prefetching;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.cache.coordinator.MySortedPrefetchListStore;
import org.greatfree.framework.cps.cache.message.PrefetchMyCachePointingListStoreNotification;

// Created: 08/03/2018, Bing Li
public class PrefetchMyPointingListStoreThread extends NotificationObjectQueue<PrefetchMyCachePointingListStoreNotification>
{

	public PrefetchMyPointingListStoreThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PrefetchMyCachePointingListStoreNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					MySortedPrefetchListStore.MIDDLE().prefetch(notification);
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
