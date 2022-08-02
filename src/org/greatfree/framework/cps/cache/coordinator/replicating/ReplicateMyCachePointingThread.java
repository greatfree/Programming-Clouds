package org.greatfree.framework.cps.cache.coordinator.replicating;

import java.io.IOException;

import org.greatfree.cache.distributed.PointingReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.Coordinator;
import org.greatfree.framework.cps.cache.data.MyCachePointing;

// Created: 07/22/2018, Bing Li
public class ReplicateMyCachePointingThread extends NotificationObjectQueue<PointingReplicateNotification<MyCachePointing>>
{

	public ReplicateMyCachePointingThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PointingReplicateNotification<MyCachePointing> notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					if (notification.getValue() != null)
					{
						Coordinator.CPS().replicate(notification.getValue());
					}
					else
					{
						Coordinator.CPS().replicateCachePointings(notification.getCacheKey(), notification.getValues());
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
