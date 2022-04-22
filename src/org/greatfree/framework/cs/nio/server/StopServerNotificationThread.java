package org.greatfree.framework.cs.nio.server;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cs.nio.message.StopServerNotification;

/**
 * 
 * @author Bing Li
 * 
 * 02/08/2022
 *
 */
class StopServerNotificationThread extends NotificationQueue<StopServerNotification>
{

	public StopServerNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		StopServerNotification notification;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.dequeue();
					MyServer.CS().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
					super.disposeMessage(notification);
				}
				catch (InterruptedException | IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				super.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
