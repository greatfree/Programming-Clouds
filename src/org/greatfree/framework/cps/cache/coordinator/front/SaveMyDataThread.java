package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedMap;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedMapStore;
import org.greatfree.framework.cps.cache.message.front.SaveMyDataNotification;

// Created: 07/09/2018, Bing Li
public class SaveMyDataThread extends NotificationQueue<SaveMyDataNotification>
{

	public SaveMyDataThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SaveMyDataNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					if (notification.getData() != null)
					{
						MyDistributedMap.MIDDLE().put(notification.getData().getKey(), notification.getData());
					}
					else
					{
						MyDistributedMapStore.MIDDLE().put(notification.getMapKey(), notification.getStoreData());
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
