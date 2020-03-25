package org.greatfree.dip.cps.cache.coordinator.evicting;

import java.io.IOException;

import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.Coordinator;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 02/25/2019, Bing Li
public class EvictMyUKValueThread extends NotificationObjectQueue<EvictedNotification<MyUKValue>>
{

	public EvictMyUKValueThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		EvictedNotification<MyUKValue> notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					
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
