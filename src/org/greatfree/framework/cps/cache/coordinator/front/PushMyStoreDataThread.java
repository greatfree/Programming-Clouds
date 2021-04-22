package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.MyDistributedStackStore;
import org.greatfree.framework.cps.cache.message.replicate.PushMyStoreDataNotification;

// Created: 08/09/2018, Bing Li
public class PushMyStoreDataThread extends NotificationQueue<PushMyStoreDataNotification>
{

	public PushMyStoreDataThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PushMyStoreDataNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					MyDistributedStackStore.MIDDLESTORE().push(notification.getData());
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
