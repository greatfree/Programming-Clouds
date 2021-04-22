package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.message.replicate.PushMyStoreDataNotification;
import org.greatfree.framework.cps.cache.terminal.MyTerminalStackStore;

// Created: 08/08/2018, Bing Li
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
					MyTerminalStackStore.BACKEND().push(notification.getData());
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
