package org.greatfree.dsf.cps.cache.coordinator.replicating;

import java.io.IOException;

import org.greatfree.cache.distributed.MapReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.Coordinator;
import org.greatfree.dsf.cps.cache.data.MyStoreData;

// Created: 08/24/2018, Bing Li
public class ReplicateMyDistributedStoreDataThread extends NotificationObjectQueue<MapReplicateNotification<MyStoreData>>
{

	public ReplicateMyDistributedStoreDataThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		MapReplicateNotification<MyStoreData> notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					if (notification.getValue() != null)
					{
						Coordinator.CPS().replicateMyStoreData(notification.getValue());
					}
					else
					{
						Coordinator.CPS().replicateMyStoreData(notification.getCacheKey(), notification.getValues());
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
