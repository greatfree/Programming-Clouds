package org.greatfree.framework.cps.cache.coordinator.replicating;

import java.io.IOException;

import org.greatfree.cache.distributed.PointingReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.framework.cps.cache.coordinator.Coordinator;
import org.greatfree.framework.cps.cache.data.MyPointing;

// Created: 07/19/2018, Bing Li
public class ReplicateMyPointingMapThread extends NotificationObjectQueue<PointingReplicateNotification<MyPointing>>
{

	public ReplicateMyPointingMapThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PointingReplicateNotification<MyPointing> notification;
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
						Coordinator.CPS().replicate(notification.getValues());
					}
					this.disposeObject(notification);
				}
				catch (InterruptedException | IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

}
