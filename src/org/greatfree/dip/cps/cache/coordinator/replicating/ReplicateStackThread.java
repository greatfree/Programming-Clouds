package org.greatfree.dip.cps.cache.coordinator.replicating;

import java.io.IOException;

import org.greatfree.cache.distributed.store.ReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.Coordinator;
import org.greatfree.dip.cps.cache.data.MyStoreData;

// Created: 08/07/2018, Bing Li
public class ReplicateStackThread extends NotificationObjectQueue<ReplicateNotification<MyStoreData>>
{

	public ReplicateStackThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ReplicateNotification<MyStoreData> notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					if (notification.getValue() != null)
					{
						Coordinator.CPS().push(notification.getValue());
					}
					else
					{
						Coordinator.CPS().push(notification.getCacheKey(), notification.getValues());
					}
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
