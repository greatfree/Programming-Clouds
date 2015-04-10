package com.greatfree.testing.coordinator.crawling;

import com.greatfree.concurrency.NotificationThreadCreatable;
import com.greatfree.testing.message.UnregisterCrawlServerNotification;

/*
 * The creator here attempts to create instances of UnregisterCrawlServerThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class UnregisterCrawlServerThreadCreator implements NotificationThreadCreatable<UnregisterCrawlServerNotification, UnregisterCrawlServerThread>
{
	@Override
	public UnregisterCrawlServerThread createNotificationThreadInstance(int taskSize)
	{
		return new UnregisterCrawlServerThread(taskSize);
	}
}
