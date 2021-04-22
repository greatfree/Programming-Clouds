package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateCachePointingNotification;
import org.greatfree.framework.cps.cache.terminal.MySortedTerminalListStore;
import org.greatfree.framework.cps.cache.terminal.MySortedTerminalMapStore;
import org.greatfree.framework.cps.cache.terminal.MyTimingTerminalMapStore;

// Created: 07/25/2018, Bing Li
public class ReplicateCachePointingThread extends NotificationQueue<ReplicateCachePointingNotification>
{

	public ReplicateCachePointingThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ReplicateCachePointingNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					if (notification.getPointing() != null)
					{
						if (notification.isTerminalMap())
						{
							MySortedTerminalMapStore.BACKEND().put(notification.getPointing());
						}
						else
						{
							MySortedTerminalListStore.BACKEND().add(notification.getPointing());
						}
					}
					else
					{
						MyTimingTerminalMapStore.BACKEND().put(notification.getTiming());
					}
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
