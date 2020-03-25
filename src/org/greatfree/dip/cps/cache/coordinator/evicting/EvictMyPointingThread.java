package org.greatfree.dip.cps.cache.coordinator.evicting;

import java.io.IOException;

import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.Coordinator;
import org.greatfree.dip.cps.cache.data.MyPointing;

// Created: 07/11/2018, Bing Li
public class EvictMyPointingThread extends NotificationObjectQueue<EvictedNotification<MyPointing>>
{

	public EvictMyPointingThread(int taskSize)
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
					System.out.println("EvictMyPointingListThread: Data = " + notification.getValue().getKey() + ", " + notification.getValue().getPoints() + ", " + notification.getValue().getDescription());
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
