package org.greatfree.cluster.root.container;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 03/04/2019, Bing Li
class IntercastNotificationThread extends NotificationQueue<IntercastNotification>
{

	public IntercastNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		IntercastNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					ClusterRoot.CONTAINER().processIntercastNotification(notification);
					this.disposeMessage(notification);
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

