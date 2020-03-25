package org.greatfree.cluster.child.container;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.Notification;

// Created: 01/13/2019, Bing Li
class ChildNotificationThread extends NotificationQueue<Notification>
{

	public ChildNotificationThread(int taskSize)
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
					notification = this.getNotification();
					/*
					 * The condition line is added to forward intercasting notifications. 04/26/2019, Bing Li
					 */
					/*
					 * I am implementing the root-based intercasting. It seems that the below lines are not necessary temporarily. 02/15/2019, Bing Li
					 */
					if (notification.getNotificationType() == MulticastMessageType.INTER_CHILDEN_NOTIFICATION)
					{
//						Child.CONTAINER().forward(notification);
						System.out.println("ChildNotificationThread: INTER_CHILDEN_NOTIFICATION is received and it will be forwarded ...");
						Child.CONTAINER().forward((InterChildrenNotification)notification);
					}
					else
					{
						Child.CONTAINER().forward(notification);
					}
					ChildServiceProvider.CHILD().processNotification(notification);
					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException | ClassNotFoundException | RemoteReadException | DistributedNodeFailedException e)
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
