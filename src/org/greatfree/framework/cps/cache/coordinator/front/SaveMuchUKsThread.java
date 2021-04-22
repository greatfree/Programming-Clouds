package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedList;
import org.greatfree.framework.cps.cache.message.front.SaveMuchUKsNotification;

// Created: 03/01/2019, Bing Li
public class SaveMuchUKsThread extends NotificationQueue<SaveMuchUKsNotification>
{

	public SaveMuchUKsThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SaveMuchUKsNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					
					MyDistributedList.MIDDLE().addAll(notification.getUKs());
					
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
