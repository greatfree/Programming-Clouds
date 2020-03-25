package org.greatfree.cache.distributed.container;

import org.greatfree.cache.StoreElement;
import org.greatfree.cache.distributed.MapReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationTask;
import org.greatfree.concurrency.reactive.NotificationTaskQueue;
import org.greatfree.data.ServerConfig;

// Created: 01/22/2019, Bing Li
class MapRelicateThread<Value extends StoreElement> extends NotificationTaskQueue<MapReplicateNotification<Value>>
{

	public MapRelicateThread(int taskSize, NotificationTask<MapReplicateNotification<Value>> task)
	{
		super(taskSize, task);
	}

	@Override
	public void run()
	{
		MapReplicateNotification<Value> notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					super.processNotification(notification);
					super.disposeObject(notification);
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
