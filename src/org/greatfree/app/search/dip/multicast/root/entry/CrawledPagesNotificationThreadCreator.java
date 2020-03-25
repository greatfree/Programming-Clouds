package org.greatfree.app.search.dip.multicast.root.entry;

import org.greatfree.app.search.dip.multicast.message.CrawledPagesNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 09/28/2018, Bing Li
class CrawledPagesNotificationThreadCreator implements NotificationThreadCreatable<CrawledPagesNotification, CrawledPagesNotificationThread>
{

	@Override
	public CrawledPagesNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new CrawledPagesNotificationThread(taskSize);
	}

}
