package org.greatfree.dip.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.message.replicate.ReplicateMyStoreDataMapStoreNotification;
import org.greatfree.dip.cps.cache.terminal.MyTerminalMapStore;

// Created: 08/25/2018, Bing Li
public class ReplicateMyStoreDataMapStoreThread extends NotificationQueue<ReplicateMyStoreDataMapStoreNotification>
{

	public ReplicateMyStoreDataMapStoreThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ReplicateMyStoreDataMapStoreNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					MyTerminalMapStore.BACKEND().put(notification.getData().getCacheKey(), notification.getData().getKey(), notification.getData());
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
