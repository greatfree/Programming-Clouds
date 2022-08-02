package org.greatfree.cache.distributed.container;

import org.greatfree.concurrency.reactive.NotificationTask;
import org.greatfree.concurrency.reactive.NotificationTaskQueue;
import org.greatfree.data.ServerConfig;

// Created: 01/22/2019, Bing Li
class MapPostfetchThread extends NotificationTaskQueue<ContainerMapPostfetchNotification>
{

	public MapPostfetchThread(int taskSize, NotificationTask<ContainerMapPostfetchNotification> task)
	{
		super(taskSize, task);
	}

	@Override
	public void run()
	{
		ContainerMapPostfetchNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
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
