package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.MySortedDistributedCacheStore;
import org.greatfree.dsf.cps.cache.coordinator.MyTimingDistributedCacheStore;
import org.greatfree.dsf.cps.cache.message.front.SaveCachePointingNotification;

// Created: 07/24/2018, Bing Li
public class SaveCachePointingThread extends NotificationQueue<SaveCachePointingNotification>
{

	public SaveCachePointingThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SaveCachePointingNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					if (notification.getPointing() != null)
					{
						System.out.println("SaveCachePointingThread: data is saved in MySortedDistributedCacheStore");
						MySortedDistributedCacheStore.MIDDLESTORE().put(notification.getMapKey(), notification.getPointing());
					}
					else
					{
						System.out.println("SaveCachePointingThread: data is saved in MyTimingDistributedCacheStore");
						MyTimingDistributedCacheStore.MIDDLESTORE().put(notification.getMapKey(), notification.getTiming());
					}
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
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
