package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MySortedDistributedMap;
import org.greatfree.framework.cps.cache.message.front.SaveMyPointingsMapNotification;

// Created: 08/01/2018, Bing Li
public class SaveMyPointingsMapThread extends NotificationQueue<SaveMyPointingsMapNotification>
{

	public SaveMyPointingsMapThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SaveMyPointingsMapNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					MySortedDistributedMap.MIDDLE().putAll(notification.getPointings());
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
