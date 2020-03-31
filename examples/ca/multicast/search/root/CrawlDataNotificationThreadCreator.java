package ca.multicast.search.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import ca.multicast.search.message.CrawlDataNotification;

// Created: 03/15/2020, Bing Li
class CrawlDataNotificationThreadCreator implements NotificationThreadCreatable<CrawlDataNotification, CrawlDataNotificationThread>
{

	@Override
	public CrawlDataNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new CrawlDataNotificationThread(taskSize);
	}

}
