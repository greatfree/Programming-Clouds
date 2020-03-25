package org.greatfree.cache.distributed.container;

import org.greatfree.cache.StoreElement;
import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationTask;
import org.greatfree.concurrency.reactive.NotificationTaskQueue;
import org.greatfree.data.ServerConfig;

// Created: 01/22/2019, Bing Li
class MapEvictThread<Value extends StoreElement> extends NotificationTaskQueue<EvictedNotification<Value>>
{

	public MapEvictThread(int taskSize, NotificationTask<EvictedNotification<Value>> task)
	{
		super(taskSize, task);
	}

	@Override
	public void run()
	{
		EvictedNotification<Value> notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					this.processNotification(notification);
					this.disposeObject(notification);
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
