package org.greatfree.dip.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.message.replicate.ReplicateMyPointingsNotification;
import org.greatfree.dip.cps.cache.terminal.MySortedTerminalList;

// Created: 07/12/2018, Bing Li
public class ReplicateMyPointingsThread extends NotificationQueue<ReplicateMyPointingsNotification>
{

	public ReplicateMyPointingsThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ReplicateMyPointingsNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					MySortedTerminalList.BACKEND().addAll(notification.getPointings());
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
