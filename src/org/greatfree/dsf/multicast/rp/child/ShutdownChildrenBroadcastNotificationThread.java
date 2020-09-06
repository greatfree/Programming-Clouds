package org.greatfree.dsf.multicast.rp.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.message.ShutdownChildrenBroadcastNotification;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.ServerStatus;

// Created: 10/22/2018, Bing Li
public class ShutdownChildrenBroadcastNotificationThread extends NotificationQueue<ShutdownChildrenBroadcastNotification>
{

	public ShutdownChildrenBroadcastNotificationThread(int taskSize)
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
					notification = this.getNotification();
					ChildMulticastor.CHILD().notify(notification);
					ServerStatus.FREE().setShutdown();
					ChildPeer.CHILD().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
					this.disposeMessage(notification);
				}
				catch (InterruptedException | IOException | ClassNotFoundException | RemoteReadException | InstantiationException | IllegalAccessException | DistributedNodeFailedException e)
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
