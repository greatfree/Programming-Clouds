package edu.chainnet.crawler.child.crawl;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import edu.chainnet.crawler.message.CrawledAuthoritiesNotification;

// Created: 04/24/2021, Bing Li
class CacheAuthorityThreadCreator implements NotificationThreadCreatable<CrawledAuthoritiesNotification, CacheAuthorityThread>
{

	@Override
	public CacheAuthorityThread createNotificationThreadInstance(int taskSize)
	{
		return new CacheAuthorityThread(taskSize);
	}

}
