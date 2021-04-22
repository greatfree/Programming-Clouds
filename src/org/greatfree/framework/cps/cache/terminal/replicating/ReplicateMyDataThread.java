package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMyDataNotification;
import org.greatfree.framework.cps.cache.terminal.MyTerminalMap;

// Created: 07/08/2018, Bing Li
public class ReplicateMyDataThread extends NotificationQueue<ReplicateMyDataNotification>
{

	public ReplicateMyDataThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ReplicateMyDataNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
//					MyTerminalMap.BACKEND().addKey(notification.getData().getKey());
					MyTerminalMap.BACKEND().put(notification.getData().getKey(), notification.getData());
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
