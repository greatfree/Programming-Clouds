package org.greatfree.app.search.dip.multicast.root.entry;

import org.greatfree.app.search.dip.multicast.message.CrawledPagesNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 09/28/2018, Bing Li
class CrawledPagesNotificationThreadCreator implements NotificationQueueCreator<CrawledPagesNotification, CrawledPagesNotificationThread>
{

	@Override
	public CrawledPagesNotificationThread createInstance(int taskSize)
	{
		return new CrawledPagesNotificationThread(taskSize);
	}

}
