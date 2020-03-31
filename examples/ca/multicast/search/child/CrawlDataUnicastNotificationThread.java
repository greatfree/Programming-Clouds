package ca.multicast.search.child;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.Tools;

import ca.multicast.search.message.CrawlDataNotification;

// Created: 03/16/2020, Bing Li
class CrawlDataUnicastNotificationThread extends NotificationQueue<CrawlDataNotification>
{

	public CrawlDataUnicastNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		CrawlDataNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					System.out.println("Crawled text: " + notification.getText());
					CrawlDB.DB().save(Tools.getHash(notification.getText()), notification.getText());
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
