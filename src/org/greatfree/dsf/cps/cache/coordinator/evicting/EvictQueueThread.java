package org.greatfree.dsf.cps.cache.coordinator.evicting;

import java.io.IOException;

import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.Coordinator;
import org.greatfree.dsf.cps.cache.data.MyStoreData;

// Created: 08/13/2018, Bing Li
public class EvictQueueThread extends NotificationObjectQueue<EvictedNotification<MyStoreData>>
{

	public EvictQueueThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		EvictedNotification<MyStoreData> notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					Coordinator.CPS().enqueue(notification.getValue());
					this.disposeObject(notification);
				}
				catch (InterruptedException | IOException e)
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
