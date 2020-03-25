package org.greatfree.testing.crawlserver;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.testing.message.StopCrawlMultiNotification;

/*
 * The code here attempts to create instances of StopCrawlThread. It works with the notification dispatcher. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StopCrawlThreadCreator implements NotificationThreadCreatable<StopCrawlMultiNotification, StopCrawlThread>
{
	// Create the instance of StopCrawlThread. 11/27/2014, Bing Li
	@Override
	public StopCrawlThread createNotificationThreadInstance(int taskSize)
	{
		return new StopCrawlThread(taskSize);
	}
}
