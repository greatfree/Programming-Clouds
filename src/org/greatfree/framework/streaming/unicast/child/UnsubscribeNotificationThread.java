package org.greatfree.framework.streaming.unicast.child;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.streaming.message.UnsubscribeNotification;

// Created: 03/23/2020, Bing Li
public class UnsubscribeNotificationThread extends NotificationQueue<UnsubscribeNotification>
{

	public UnsubscribeNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		UnsubscribeNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					System.out.println("UnsubscribeNotificationThread: streamKey = " + notification.getStreamKey());
					System.out.println("UnsubscribeNotificationThread: subscriber = " + notification.getSubscriber());
					StreamRegistry.UNICAST().removeSubscriber(notification.getStreamKey(), notification.getSubscriber());
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
