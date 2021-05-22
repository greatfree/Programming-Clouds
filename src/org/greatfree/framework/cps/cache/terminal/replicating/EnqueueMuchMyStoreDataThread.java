package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.message.replicate.EnqueueMuchMyStoreDataNotification;
import org.greatfree.framework.cps.cache.terminal.MyTerminalQueueStore;

// Created: 08/13/2018, Bing Li
public class EnqueueMuchMyStoreDataThread extends NotificationQueue<EnqueueMuchMyStoreDataNotification>
{

	public EnqueueMuchMyStoreDataThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		EnqueueMuchMyStoreDataNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					MyTerminalQueueStore.BACKEND().enqueueAll(notification.getQueueKey(), notification.getData());
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
