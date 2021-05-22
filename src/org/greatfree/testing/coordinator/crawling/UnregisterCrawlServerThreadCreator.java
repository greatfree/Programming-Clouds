package org.greatfree.testing.coordinator.crawling;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.UnregisterCrawlServerNotification;

/*
 * The creator here attempts to create instances of UnregisterCrawlServerThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class UnregisterCrawlServerThreadCreator implements NotificationQueueCreator<UnregisterCrawlServerNotification, UnregisterCrawlServerThread>
{
	@Override
	public UnregisterCrawlServerThread createInstance(int taskSize)
	{
		return new UnregisterCrawlServerThread(taskSize);
	}
}
