package org.greatfree.dsf.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.message.replicate.ReplicateMuchMyStoreDataMapStoreNotification;
import org.greatfree.dsf.cps.cache.terminal.MyTerminalMapStore;

// Created: 08/25/2018, Bing Li
public class ReplicateMuchMyStoreDataMapStoreThread extends NotificationQueue<ReplicateMuchMyStoreDataMapStoreNotification>
{

	public ReplicateMuchMyStoreDataMapStoreThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ReplicateMuchMyStoreDataMapStoreNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					MyTerminalMapStore.BACKEND().putAll(notification.getMapKey(), notification.getData());
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
