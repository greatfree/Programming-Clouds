package edu.chainnet.crawler.child.crawl;

import org.greatfree.concurrency.reactive.NotificationQueue;

import edu.chainnet.crawler.CrawlConfig;
import edu.chainnet.crawler.message.CrawledAuthoritiesNotification;

// Created: 04/24/2021, Bing Li
class CacheAuthorityThread extends NotificationQueue<CrawledAuthoritiesNotification>
{

	public CacheAuthorityThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		CrawledAuthoritiesNotification notification;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					notification = this.getNotification();
					PersistedRawPagesQueue.WWW().saveAuthorities(notification.getAuthorities());
					this.disposeMessage(notification);
				}
				catch (InterruptedException | IllegalStateException e)
				{
//					ServerStatus.FREE().printException(e);
					CrawlPrompts.print(e);
				}
			}
			try
			{
				this.holdOn(CrawlConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

}
