package org.greatfree.dip.cps.cache.coordinator.replicating;

import java.io.IOException;

import org.greatfree.cache.distributed.MapReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.Coordinator;
import org.greatfree.dip.cps.cache.data.MyData;

// Created: 07/08/2018,Bing Li
public class ReplicateMyDataThread extends NotificationObjectQueue<MapReplicateNotification<MyData>>
{

	public ReplicateMyDataThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		MapReplicateNotification<MyData> notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					if (notification.getValue() != null)
					{
						Coordinator.CPS().replicate(notification.getValue());
					}
					else
					{
//						MyDistributedMap.MIDDLE().addKeys(notification.getValues().keySet());
						Coordinator.CPS().replicate(notification.getValues());
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
