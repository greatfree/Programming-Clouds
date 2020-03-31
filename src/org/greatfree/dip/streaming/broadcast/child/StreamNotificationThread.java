package org.greatfree.dip.streaming.broadcast.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.child.ChildMulticastor;
import org.greatfree.dip.streaming.message.StreamNotification;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

// Created: 03/19/2020, Bing Li
class StreamNotificationThread extends NotificationQueue<StreamNotification>
{

	public StreamNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		StreamNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					System.out.println("StreamNotificationThread: stream received: " + notification.getData());
					ChildMulticastor.CHILD().asyncNotify(notification);

					System.out.println("StreamNotificationThread: selected child key = " + notification.getChildKey());
					System.out.println("StreamNotificationThread: local key = " + ChildPeer.BROADCAST().getLocalKey());

					// Only the randomly selected child is responsible for multicasting data to subscribers. 03/21/2020, Bing Li
					if (notification.getChildKey().equals(ChildPeer.BROADCAST().getLocalKey()))
					{
						if (RootMulticastor.CHILD_STREAM().resetChildren(ChildPeer.BROADCAST().getSubscriberIPs(notification.getData().getPublisher(), notification.getData().getTopic())))
						{
							RootMulticastor.CHILD_STREAM().broadcastNotify(notification);
						}
					}

					this.disposeMessage(notification);
				}
				catch (InterruptedException | ClassNotFoundException | RemoteReadException | IOException | InstantiationException | IllegalAccessException | DistributedNodeFailedException e)
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
