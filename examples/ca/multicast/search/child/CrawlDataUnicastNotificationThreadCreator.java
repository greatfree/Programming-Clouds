package ca.multicast.search.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import ca.multicast.search.message.CrawlDataNotification;

// Created: 03/16/2020, Bing Li
class CrawlDataUnicastNotificationThreadCreator implements NotificationThreadCreatable<CrawlDataNotification, CrawlDataUnicastNotificationThread>
{

	@Override
	public CrawlDataUnicastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new CrawlDataUnicastNotificationThread(taskSize);
	}

}
