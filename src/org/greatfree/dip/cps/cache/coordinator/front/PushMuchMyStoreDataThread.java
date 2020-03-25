package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.MyDistributedStackStore;
import org.greatfree.dip.cps.cache.message.replicate.PushMuchMyStoreDataNotification;

// Created: 08/09/2018, Bing Li
public class PushMuchMyStoreDataThread extends NotificationQueue<PushMuchMyStoreDataNotification>
{

	public PushMuchMyStoreDataThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PushMuchMyStoreDataNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					MyDistributedStackStore.MIDDLESTORE().pushAll(notification.getStackKey(), notification.getData());
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
