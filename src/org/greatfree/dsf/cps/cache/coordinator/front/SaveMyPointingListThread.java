package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.MySortedDistributedList;
import org.greatfree.dsf.cps.cache.message.front.SaveMyPointingListNotification;

// Created: 07/13/2018, Bing Li
public class SaveMyPointingListThread extends NotificationQueue<SaveMyPointingListNotification>
{

	public SaveMyPointingListThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SaveMyPointingListNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					MySortedDistributedList.MIDDLE().add(notification.getPointing());
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
