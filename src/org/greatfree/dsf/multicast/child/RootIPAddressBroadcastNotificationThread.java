package org.greatfree.dsf.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.message.RootIPAddressBroadcastNotification;

// Created: 09/08/2018, Bing Li
class RootIPAddressBroadcastNotificationThread extends NotificationQueue<RootIPAddressBroadcastNotification>
{

	public RootIPAddressBroadcastNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		RootIPAddressBroadcastNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					
//					System.out.println("RootIPAddressBroadcastNotificationThread: root Address: " + notification.getRootAddress().getIP() + ":" + notification.getRootAddress().getPort());
					
					/*
					Map<String, IPAddress> addresses= notification.getChildrenIPs();
	
					if (addresses != null)
					{
						for (IPAddress entry : addresses.values())
						{
							System.out.println("RootIPAddressBroadcastNotificationThread: child Address: " + entry.getIP() + ":" + entry.getPort());
						}
					}
					else
					{
						System.out.println("RootIPAddressBroadcastNotificationThread: the current node has no children!");
					}
					*/
					
					ChildMulticastor.CHILD().asyncNotify(notification);
					ChildPeer.CHILD().setRootIP(notification.getRootAddress());
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
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
