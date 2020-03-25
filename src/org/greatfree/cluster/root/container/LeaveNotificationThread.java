package org.greatfree.cluster.root.container;

import java.io.IOException;

import org.greatfree.cluster.message.LeaveNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;

// Created: 01/13/2019, Bing Li
class LeaveNotificationThread extends NotificationQueue<LeaveNotification>
{

	public LeaveNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		LeaveNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					Clustering.removeChild(notification.getChildID());
					this.disposeMessage(notification);
				}
				catch (InterruptedException | ClassNotFoundException | RemoteReadException | IOException e)
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
