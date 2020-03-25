package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.MySortedDistributedList;
import org.greatfree.dip.cps.cache.message.front.SaveMyPointingsListNotification;

// Created: 07/28/2018, Bing Li
public class SaveMyPointingsListThread extends NotificationQueue<SaveMyPointingsListNotification>
{

	public SaveMyPointingsListThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SaveMyPointingsListNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					MySortedDistributedList.MIDDLE().addAll(notification.getPointings());
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
