package org.greatfree.testing.crawlserver;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.message.MulticastMessageDisposer;
import org.greatfree.testing.message.StartCrawlMultiNotification;

/*
 * The creator aims to generate the instance of MulticastStartCrawlNotificationThread for the BoundNotificationDispatcher so as to schedule the notification as tasks concurrently. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class MulticastStartCrawlNotificationThreadCreator implements BoundNotificationThreadCreatable<StartCrawlMultiNotification, MulticastMessageDisposer<StartCrawlMultiNotification>, MulticastStartCrawlNotificationThread>
{
	@Override
	public MulticastStartCrawlNotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MulticastMessageDisposer<StartCrawlMultiNotification> binder)
	{
		return new MulticastStartCrawlNotificationThread(taskSize, dispatcherKey, binder);
	}
}
