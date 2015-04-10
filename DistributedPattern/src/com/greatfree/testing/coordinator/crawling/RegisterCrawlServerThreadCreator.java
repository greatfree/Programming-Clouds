package com.greatfree.testing.coordinator.crawling;

import com.greatfree.concurrency.NotificationThreadCreatable;
import com.greatfree.testing.message.RegisterCrawlServerNotification;

/*
 * The creator here attempts to create instances of RegisterCrawlServerThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class RegisterCrawlServerThreadCreator implements NotificationThreadCreatable<RegisterCrawlServerNotification, RegisterCrawlServerThread>
{
	@Override
	public RegisterCrawlServerThread createNotificationThreadInstance(int taskSize)
	{
		return new RegisterCrawlServerThread(taskSize);
	}
}
