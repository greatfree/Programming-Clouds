package org.greatfree.testing.crawlserver;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.message.MulticastMessageDisposer;
import org.greatfree.testing.message.StartCrawlMultiNotification;

/*
 * This is an implementation of the interface BoundNotificationThreadCreatable to create the instance of StartCrawlThread inside the pool, BoundNotificationDispatcher. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public class StartCrawlThreadCreator implements BoundNotificationThreadCreatable<StartCrawlMultiNotification, MulticastMessageDisposer<StartCrawlMultiNotification>, StartCrawlThread>
{
	@Override
	public StartCrawlThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MulticastMessageDisposer<StartCrawlMultiNotification> binder)
	{
		return new StartCrawlThread(taskSize, dispatcherKey, binder);
	}
}
