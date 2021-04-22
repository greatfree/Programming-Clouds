package org.greatfree.framework.cps.cache.coordinator.evicting;

import java.io.IOException;

import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.Coordinator;
import org.greatfree.framework.cps.cache.data.MyPointing;

// Created: 07/19/2018, Bing Li
public class EvictMyPointingMapThread extends NotificationObjectQueue<EvictedNotification<MyPointing>>
{

	public EvictMyPointingMapThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		EvictedNotification<MyPointing> notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					System.out.println("EvictMyPointingMapThread: Data = " + notification.getValue().getKey() + ", " + notification.getValue().getPoints() + ", " + notification.getValue().getDescription());
					Coordinator.CPS().replicate(notification.getValue());
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
