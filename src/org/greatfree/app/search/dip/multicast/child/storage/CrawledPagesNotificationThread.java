package org.greatfree.app.search.dip.multicast.child.storage;

import org.greatfree.app.search.dip.multicast.message.CrawledPagesNotification;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;

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
					notification = this.getNotification();
					PageStorage.STORAGE().save(notification.getPage());
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
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
