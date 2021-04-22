package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMyPointingNotification;
import org.greatfree.framework.cps.cache.terminal.MySortedTerminalList;

// Created: 07/12/2018, Bing Li
public class ReplicateMyPointingThread extends NotificationQueue<ReplicateMyPointingNotification>
{

	public ReplicateMyPointingThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ReplicateMyPointingNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					MySortedTerminalList.BACKEND().add(notification.getPointing());
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
