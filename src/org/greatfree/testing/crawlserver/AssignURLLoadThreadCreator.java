package org.greatfree.testing.crawlserver;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.CrawlLoadNotification;

/*
 * The creator here attempts to create instances of AssignURLLoadThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class AssignURLLoadThreadCreator implements NotificationQueueCreator<CrawlLoadNotification, AssignURLLoadThread>
{
	@Override
	public AssignURLLoadThread createInstance(int taskSize)
	{
		return new AssignURLLoadThread(taskSize);
	}
}
