package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.MySortedDistributedMap;
import org.greatfree.dip.cps.cache.message.front.SaveMyPointingMapNotification;

// Created: 07/20/2018, Bing Li
public class SaveMyPointingMapThread extends NotificationQueue<SaveMyPointingMapNotification>
{

	public SaveMyPointingMapThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SaveMyPointingMapNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					MySortedDistributedMap.MIDDLE().put(notification.getPointing());
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
