package org.greatfree.dip.cps.cache.coordinator.replicating;

import java.io.IOException;

import org.greatfree.cache.distributed.PointingReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.Coordinator;
import org.greatfree.dip.cps.cache.data.MyCacheTiming;

// Created: 08/19/2018, Bing Li
public class ReplicateMyCacheTimingThread extends NotificationObjectQueue<PointingReplicateNotification<MyCacheTiming>>
{

	public ReplicateMyCacheTimingThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PointingReplicateNotification<MyCacheTiming> notification;
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
						Coordinator.CPS().replicateCacheTimings(notification.getCacheKey(), notification.getValues());
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
