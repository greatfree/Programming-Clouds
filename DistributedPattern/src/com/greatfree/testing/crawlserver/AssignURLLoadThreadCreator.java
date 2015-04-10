package com.greatfree.testing.crawlserver;

import com.greatfree.concurrency.NotificationThreadCreatable;
import com.greatfree.testing.message.CrawlLoadNotification;

/*
 * The creator here attempts to create instances of AssignURLLoadThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class AssignURLLoadThreadCreator implements NotificationThreadCreatable<CrawlLoadNotification, AssignURLLoadThread>
{
	@Override
	public AssignURLLoadThread createNotificationThreadInstance(int taskSize)
	{
		return new AssignURLLoadThread(taskSize);
	}
}
