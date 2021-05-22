package org.greatfree.framework.multicast.rp.child;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.multicast.RPMulticastResponse;

// Created: 10/22/2018, Bing Li
public class ChildRPMulticastResponseThread extends NotificationQueue<RPMulticastResponse>
{

	public ChildRPMulticastResponseThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		RPMulticastResponse notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					ChildMulticastor.CHILD().getRP().saveResponse(notification);
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
