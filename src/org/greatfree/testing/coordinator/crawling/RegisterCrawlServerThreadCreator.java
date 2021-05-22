package org.greatfree.testing.coordinator.crawling;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.RegisterCrawlServerNotification;

/*
 * The creator here attempts to create instances of RegisterCrawlServerThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class RegisterCrawlServerThreadCreator implements NotificationQueueCreator<RegisterCrawlServerNotification, RegisterCrawlServerThread>
{
	@Override
	public RegisterCrawlServerThread createInstance(int taskSize)
	{
		return new RegisterCrawlServerThread(taskSize);
	}
}
