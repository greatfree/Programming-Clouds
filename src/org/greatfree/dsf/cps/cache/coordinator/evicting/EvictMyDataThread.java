package org.greatfree.dsf.cps.cache.coordinator.evicting;

import java.io.IOException;

import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.Coordinator;
import org.greatfree.dsf.cps.cache.data.MyData;

// Created: 07/09/2018, Bing Li
public class EvictMyDataThread extends NotificationObjectQueue<EvictedNotification<MyData>>
{

	public EvictMyDataThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		EvictedNotification<MyData> notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					System.out.println("EvictMyDataThread: " + notification.getValue().getKey() + " is evicted ...");
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
