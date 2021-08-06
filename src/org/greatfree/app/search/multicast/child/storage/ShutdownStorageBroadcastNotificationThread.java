package org.greatfree.app.search.multicast.child.storage;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.child.ChildMulticastor;
import org.greatfree.framework.multicast.message.ShutdownChildrenBroadcastNotification;
import org.greatfree.util.ServerStatus;

// Created: 12/10/2018, Bing Li
class ShutdownStorageBroadcastNotificationThread extends NotificationQueue<ShutdownChildrenBroadcastNotification>
{

	public ShutdownStorageBroadcastNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ShutdownChildrenBroadcastNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
					ChildMulticastor.CHILD().notify(notification);
					ServerStatus.FREE().setShutdown();
					StorageNode.STORAGE().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
					this.disposeMessage(notification);
				}
				catch (InterruptedException | InstantiationException | IllegalAccessException | IOException | DistributedNodeFailedException | ClassNotFoundException | RemoteReadException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 11/26/2014, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		
	}

}
