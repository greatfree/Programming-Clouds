package org.greatfree.cluster.child.container;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.message.multicast.container.IntercastNotification;

/*
 * Now I need to implement the root based intercasting. So the thread is not necessary temporarily. I will implement the children-based intercasing later. 02/15/2019, Bing Li 
 */

// Created: 01/26/2019, Bing Li
class IntercastNotificationThread extends NotificationQueue<IntercastNotification>
{

	public IntercastNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		IntercastNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					/*
					 * An intercast notification is forwarded only once from the source child to the destination child. So if it is forwarded, it indicates that the local child is the destination. 02/10/2019, Bing Li
					 */
					/*
					if (!notification.isForwarded())
					{
						notification.setForwarded();
						Child.CONTAINER().forward(notification);
					}
					else
					{
						ChildServiceProvider.CHILD().processNotification(notification);
					}
					*/
					ChildServiceProvider.CHILD().processIntercastNotification(notification);
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
