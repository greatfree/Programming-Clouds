package org.greatfree.dsf.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.message.replicate.ReplicateMyUKValueNotification;
import org.greatfree.dsf.cps.cache.terminal.MyTerminalList;

// Created: 02/27/2019, Bing Li
public class ReplicateMyUKValuesThread extends NotificationQueue<ReplicateMyUKValueNotification>
{

	public ReplicateMyUKValuesThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ReplicateMyUKValueNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					
					if (notification.getValue() != null)
					{
						MyTerminalList.BACKEND().add(notification.getValue());
					}
					else
					{
						MyTerminalList.BACKEND().addAll(notification.getValues());
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
