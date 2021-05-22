package org.greatfree.app.search.dip.multicast.root.entry;

import java.io.IOException;

import org.greatfree.app.search.dip.multicast.message.CrawledPagesNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.framework.multicast.root.RootMulticastor;

// Created: 09/28/2018, Bing Li
class CrawledPagesNotificationThread extends NotificationQueue<CrawledPagesNotification>
{

	public CrawledPagesNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		CrawledPagesNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.dequeue();
//					RootMulticastor.ROOT().broadcastNotify(notification);
					RootMulticastor.ROOT().unicastNotify(notification);
					this.disposeMessage(notification);
				}
				catch (InterruptedException | InstantiationException | IllegalAccessException | IOException | DistributedNodeFailedException e)
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
