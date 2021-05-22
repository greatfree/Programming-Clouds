package org.greatfree.server.container;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.container.Notification;
import org.greatfree.util.ServerStatus;

// Created: 12/18/2018, Bing Li
class NotificationThread extends NotificationQueue<Notification>
{

	public NotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		Notification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					ServiceProvider.CS().processNotification(super.getServerKey(), notification);
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					ServerStatus.FREE().printException(e);
				}
			}
			try
			{
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				ServerStatus.FREE().printException(e);
			}
		}
		
	}

}
