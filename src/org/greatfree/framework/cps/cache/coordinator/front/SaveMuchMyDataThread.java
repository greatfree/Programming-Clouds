package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedMap;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedMapStore;
import org.greatfree.framework.cps.cache.message.front.SaveMuchMyDataNotification;

// Created: 07/28/2018, Bing Li
public class SaveMuchMyDataThread extends NotificationQueue<SaveMuchMyDataNotification>
{

	public SaveMuchMyDataThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SaveMuchMyDataNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					if (notification.getData() != null)
					{
//						MyDistributedMap.MIDDLE().addKeys(notification.getData().keySet());
						MyDistributedMap.MIDDLE().putAll(notification.getData());
					}
					else
					{
						MyDistributedMapStore.MIDDLE().putAll(notification.getMapKey(), notification.getStoreData());
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
