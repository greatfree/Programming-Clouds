package com.greatfree.testing.coordinator.crawling;

import com.greatfree.concurrency.NotificationThreadCreatable;
import com.greatfree.testing.message.CrawledLinksNotification;

/*
 * The creator here attempts to create instances of DistributeLinksThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class DistributeLinksThreadCreator implements NotificationThreadCreatable<CrawledLinksNotification, DistributeLinksThread>
{
	@Override
	public DistributeLinksThread createNotificationThreadInstance(int taskSize)
	{
		return new DistributeLinksThread(taskSize);
	}
}
