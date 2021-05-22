package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MySortedDistributedCacheStore;
import org.greatfree.framework.cps.cache.coordinator.MyTimingDistributedCacheStore;
import org.greatfree.framework.cps.cache.message.front.SaveCachePointingsNotification;

// Created: 07/24/2018, Bing Li
public class SaveCachePointingsThread extends NotificationQueue<SaveCachePointingsNotification>
{

	public SaveCachePointingsThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SaveCachePointingsNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					if (notification.getPointings() != null)
					{
						MySortedDistributedCacheStore.MIDDLESTORE().putAll(notification.getCacheKey(), notification.getPointings());
					}
					else
					{
						MyTimingDistributedCacheStore.MIDDLESTORE().putAll(notification.getCacheKey(), notification.getTimings());
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
