package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedList;
import org.greatfree.framework.cps.cache.message.front.SaveMyUKNotification;

// Created: 03/01/2019, Bing Li
public class SaveMyUKThread extends NotificationQueue<SaveMyUKNotification>
{

	public SaveMyUKThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SaveMyUKNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					MyDistributedList.MIDDLE().add(notification.getUK());
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
