package org.greatfree.framework.cs.nio.server;

import java.util.logging.Logger;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cs.nio.message.MyNotification;

/**
 * 
 * @author Bing Li
 * 
 * 02/05/2022
 *
 */
class MyNotificationThread extends NotificationQueue<MyNotification>
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.cs.nio.server");

	public MyNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		MyNotification notification;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.dequeue();
					log.info(notification.getMessage());
					super.disposeMessage(notification);
				}
				catch (InterruptedException e)
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
