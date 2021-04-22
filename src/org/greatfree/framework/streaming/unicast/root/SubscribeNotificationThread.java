package org.greatfree.framework.streaming.unicast.root;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.framework.streaming.message.SubscribeNotification;

// Created: 03/23/2020, Bing Li
class SubscribeNotificationThread extends NotificationQueue<SubscribeNotification>
{

	public SubscribeNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SubscribeNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					RootMulticastor.UNI_STREAM().unicastNotify(notification.getStreamKey(), notification);
					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException | DistributedNodeFailedException e)
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
