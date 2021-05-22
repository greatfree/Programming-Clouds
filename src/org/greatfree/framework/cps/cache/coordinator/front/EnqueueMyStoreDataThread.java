package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedQueueStore;
import org.greatfree.framework.cps.cache.message.replicate.EnqueueMyStoreDataNotification;

// Created: 08/13/2018, Bing Li
public class EnqueueMyStoreDataThread extends NotificationQueue<EnqueueMyStoreDataNotification>
{

	public EnqueueMyStoreDataThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		EnqueueMyStoreDataNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					MyDistributedQueueStore.MIDDLESTORE().enqueue(notification.getData());
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
