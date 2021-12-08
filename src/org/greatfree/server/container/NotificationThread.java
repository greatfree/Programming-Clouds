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
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.dequeue();
					ServiceProvider.CS().processNotification(super.getServerKey(), notification);
					super.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					ServerStatus.FREE().printException(e);
				}
			}
			try
			{
				/*
				 * It is not necessary since the shutdown state is judged in the loop immediately. 12/01/2021, Bing Li
				 * 
				 * I modified the code. If the thread needs to be shutdown, it is not necessary to keep it in the loops. 12/01/2021, Bing Li
				 */
				
				/*
				if (!super.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME))
				{
					return;
				}
				*/
				super.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				ServerStatus.FREE().printException(e);
			}
		}
		
	}

}
