package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMuchMyDataNotification;
import org.greatfree.framework.cps.cache.terminal.MyTerminalMap;

// Created: 07/30/2018, Bing Li
public class ReplicateMuchMyDataThread extends NotificationQueue<ReplicateMuchMyDataNotification>
{

	public ReplicateMuchMyDataThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ReplicateMuchMyDataNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
//					MyTerminalMap.BACKEND().addKeys(notification.getData().keySet());
					MyTerminalMap.BACKEND().putAll(notification.getData());
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
