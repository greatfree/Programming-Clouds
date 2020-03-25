package org.greatfree.dip.cps.cache.coordinator.replicating;

import java.io.IOException;

import org.greatfree.cache.distributed.PointingReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.Coordinator;
import org.greatfree.dip.cps.cache.data.MyPointing;

// Created: 07/11/2018, Bing Li
public class ReplicateMyPointingThread extends NotificationObjectQueue<PointingReplicateNotification<MyPointing>>
{

	public ReplicateMyPointingThread(int taskSize)
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
					notification = this.getNotification();
					if (notification.getValue() != null)
					{
						System.out.println("ReplicateMyPointingThread: " + notification.getValue().getDescription());
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
