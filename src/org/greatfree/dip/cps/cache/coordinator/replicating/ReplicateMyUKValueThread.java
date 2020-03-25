package org.greatfree.dip.cps.cache.coordinator.replicating;

import java.io.IOException;

import org.greatfree.cache.distributed.ListReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.coordinator.Coordinator;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 02/25/2019, Bing Li
public class ReplicateMyUKValueThread extends NotificationObjectQueue<ListReplicateNotification<MyUKValue>>
{

	public ReplicateMyUKValueThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ListReplicateNotification<MyUKValue> notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					
					if (notification.getValue() != null)
					{
						Coordinator.CPS().replicate(notification.getValue());
					}
					else
					{
						Coordinator.CPS().replicateUKs(notification.getValues());
					}
					this.disposeObject(notification);
				}
				catch (InterruptedException | IOException e)
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
