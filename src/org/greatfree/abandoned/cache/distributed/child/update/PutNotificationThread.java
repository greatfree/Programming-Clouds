package org.greatfree.abandoned.cache.distributed.child.update;

import org.greatfree.cache.message.update.PutNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

// Created: 07/17/2017, Bing Li
public class PutNotificationThread extends NotificationQueue<PutNotification>
{

	public PutNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PutNotification notification;
		DistributedPersistableChildMap map;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					
					map = ChildMapRegistry.CACHE().getMap(notification.getValue().getCacheKey());
					if (map != null)
					{
						map.put(notification.getValue().getDataKey(), notification.getValue());
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
				// Wait for a moment after all of the existing notifications are processed. 01/20/2016, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

}
