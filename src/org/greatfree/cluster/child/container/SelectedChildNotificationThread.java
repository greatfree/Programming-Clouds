package org.greatfree.cluster.child.container;

import java.io.IOException;

import org.greatfree.cluster.message.SelectedChildNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

// Since the message, SelectedChildNotification, needs to be forwarded/broadcast, it inherits Notification. The message is processed in the thread,  ChildNotificationThread. So the below lines is NOT useful. 09/12/2020, Bing Li

// Created: 09/06/2020, Bing Li
class SelectedChildNotificationThread extends NotificationQueue<SelectedChildNotification>
{

	public SelectedChildNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SelectedChildNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					Child.CONTAINER().leaveCluster();
//					System.out.println("SelectedChildNotificationThread: cluster root IP = " + notification.getClusterRootIP());
					Child.CONTAINER().reset(notification.getRootKey(), notification.getClusterRootIP());
					Child.CONTAINER().joinCluster(notification.getClusterRootIP().getIP(), notification.getClusterRootIP().getPort());
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
