package org.greatfree.testing.coordinator.crawling;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.CrawledLinksNotification;

/*
 * The creator here attempts to create instances of DistributeLinksThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class DistributeLinksThreadCreator implements NotificationQueueCreator<CrawledLinksNotification, DistributeLinksThread>
{
	@Override
	public DistributeLinksThread createInstance(int taskSize)
	{
		return new DistributeLinksThread(taskSize);
	}
}
