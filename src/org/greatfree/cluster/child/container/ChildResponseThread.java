package org.greatfree.cluster.child.container;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.multicast.container.ChildResponse;

// Created: 03/04/2019, Bing Li
class ChildResponseThread extends NotificationQueue<ChildResponse>
{
	public ChildResponseThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ChildResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					response = this.getNotification();
					Child.CONTAINER().saveResponse(response);
					this.disposeMessage(response);
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


