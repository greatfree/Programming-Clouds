package org.greatfree.cluster.root.container;

import java.io.IOException;

import org.greatfree.cluster.message.JoinNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;

// Created: 01/13/2019, Bing Li
class JoinNotificationThread extends NotificationQueue<JoinNotification>
{

	public JoinNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		JoinNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
//					System.out.println("1) JoinNotificationThread ......");
					Clustering.addChild(notification.getChildID());
//					System.out.println("2) JoinNotificationThread ......");
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
